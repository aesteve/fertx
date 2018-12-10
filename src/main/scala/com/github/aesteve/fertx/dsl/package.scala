package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl.extractors.{QueryParamExtractor, _}
import com.github.aesteve.fertx.dsl.path.{PathFragmentDefinition, _}
import com.github.aesteve.fertx.dsl.routing.RouteDefinition
import com.github.aesteve.fertx.dsl.routing.impl.RouteDefinitionImpl
import com.github.aesteve.fertx.request.RequestType
import com.github.aesteve.fertx.response.{BadRequest, ClientError, NoContentErrorMarshaller, ResponseType}
import com.timeout.docless.swagger.{Operation, Parameter}
import io.vertx.core.http.HttpMethod

import scala.util.Try

package object dsl {

  type PathFragDef0 = PathFragmentDefinition[Unit]
  type PathFragDef1[T] = PathFragmentDefinition[Tuple1[T]]
  type QueryParam1[T] = QueryParamExtractor[Tuple1[T]]

  def GET[T](pathDef: PathDefinition[T]): RouteDefinition[T, request.NoContent, response.NoContent] =
    new RouteDefinitionImpl(HttpMethod.GET, pathDef, pathDef.extractor, RequestType.NO_CONTENT, ResponseType.NO_CONTENT, NoContentErrorMarshaller)

  def POST[T](pathDef: PathDefinition[T]): RouteDefinition[T, request.NoContent, response.NoContent] =
    new RouteDefinitionImpl(HttpMethod.POST, pathDef, pathDef.extractor, RequestType.NO_CONTENT, ResponseType.NO_CONTENT, NoContentErrorMarshaller)


  object * extends PathFragDef0 {

    override def captures: Boolean = false

    override def toRegex: String = ".*"

    override def fromParam: Option[String] => Either[ClientError, Unit] = _ => Right(())

    override def buildOpenAPI(operation: Operation): Operation =
      operation
  }

  case class FixedPath(str: String) extends PathFragDef0 {

    override def captures: Boolean = false

    override def toRegex: String =
      str

    override def fromParam: Option[String] => Either[ClientError, Unit] =
      _ => Right(())

    override def buildOpenAPI(operation: Operation): Operation =
      operation

  }

  object IntPath extends PathFragDef1[Int] {

    override def captures: Boolean = true

    override def toRegex: String =
      "(\\d+)"

    override def fromParam: Option[String] => Either[ClientError, Tuple1[Int]] =
      strToInt

    override def buildOpenAPI(operation: Operation): Operation = {
      val param = Parameter
        .path("path")
          .as[Int]
      operation.withParams(param)
    }

  }

  object StrPath extends PathFragDef1[String] {

    override def captures: Boolean = true

    override def toRegex: String =
      "(\\w+)"

    override def fromParam: Option[String] => Either[ClientError, Tuple1[String]] = {
      case None => Left(BadRequest("Path parameter absent"))
      case Some(str) => Right(Tuple1(str))
    }


    override def buildOpenAPI(operation: Operation): Operation = {
      val param = Parameter.path("")
      operation.withParams(param)
    }

  }


  class Param[T](name: String, fun: Option[String] => Either[ClientError, T], handleParam: Parameter => Parameter) extends QueryParam1[T](name, handleParam) {
    override def fromReq: Option[String] => Either[ClientError, Tuple1[T]] =
      s => fun(s) match {
        case Right(value) =>
          Right(Tuple1(value))
        case Left(err) =>
          Left(err)
      }
  }

  case class StrParam(name: String) extends Param[String](name, {
    case None =>
      Left(BadRequest(s"Query parameter $name expected"))
    case Some(value) =>
      Right(value)
  }, param => {
    param.as[String]
    param
  })

  case class StrParamOpt(name: String) extends Param[Option[String]](name,  {
      case None => Right(None)
      case Some(value) => Right(Some(value))
  }, param => {
    param.as[String]
    param
  })

  case class IntParam(name: String) extends QueryParam1[Int](name, param => {
    param.as[String]
    param
  }) {
    override def fromReq: Option[String] => Either[ClientError, Tuple1[Int]] =
      strToInt
  }

  case class IntParamOpt(name: String) extends QueryParam1[Option[Int]](name, param => {
    param.as[String]
    param

  }) {
    override def fromReq: Option[String] => Either[ClientError, Tuple1[Option[Int]]] = {
      case None =>
        Right(Tuple1(None))
      case Some(str) =>
        Try(str.toInt).fold(_ => Left(BadRequest(s"Could not convert $str to Int")), i => Right(Tuple1(Some(i))))
    }

  }


  implicit def pathFragDefFromStr(str: String): FixedPath =
    FixedPath(str)

  implicit def pathDefFromStr(str: String): PathDefinition[Unit] =
    PathDefinition(str, FixedPath(str).at(0))

  private def strToInt: Option[String] => Either[ClientError, Tuple1[Int]] = {
    case None =>
      Left(BadRequest("Parameter is mandatory"))
    case Some(str) =>
      Try(str.toInt).fold(_ => Left(BadRequest(s"Could not convert $str to Int")), i => Right(Tuple1(i)))
  }

}
