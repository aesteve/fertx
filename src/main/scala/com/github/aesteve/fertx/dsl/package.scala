package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl.extractors.QueryParamExtractor
import com.github.aesteve.fertx.dsl.path.{PathFragmentDefinition, _}
import com.github.aesteve.fertx.dsl.routing.RouteDefinition
import com.github.aesteve.fertx.dsl.routing.impl.RouteDefinitionImpl
import com.github.aesteve.fertx.media._
import com.github.aesteve.fertx.response.{BadRequest, ClientError, UnitErrorMarshaller}
import io.swagger.v3.oas.models.media.{IntegerSchema, StringSchema}
import io.swagger.v3.oas.models.parameters.{Parameter, PathParameter}
import io.vertx.core.http.HttpMethod

import scala.util.Try

package object dsl {

  type QueryParam1[T] = QueryParamExtractor[Tuple1[T]]

  def request[T](method: HttpMethod, pathDef: PathDefinition[T]): RouteDefinition[T, Unit, Unit] =
    new RouteDefinitionImpl(method, pathDef, pathDef.extractor, UnitErrorMarshaller)

  def GET[T](pathDef: PathDefinition[T]): RouteDefinition[T, Unit, Unit] =
    request(HttpMethod.GET, pathDef)

  def POST[T](pathDef: PathDefinition[T]): RouteDefinition[T, Unit, Unit] =
    request(HttpMethod.POST, pathDef)

  object * extends UnitPathFragment("*")
  case class FixedPath(str: String) extends UnitPathFragment(str)

  case class IntPath(name: String) extends CapturesPathFragment[Tuple1[Int]](new PathParameter().name(name).schema(new IntegerSchema)) {

    override def fromParam: Option[String] => Either[ClientError, Tuple1[Int]] =
      strToInt

  }

  case class StrPath(name: String) extends CapturesPathFragment[Tuple1[String]](new PathParameter().name(name).schema(new StringSchema)) {

    override def fromParam: Option[String] => Either[ClientError, Tuple1[String]] = opt =>
      Right(Tuple1(opt.get))

  }


  class Param[T](name: String, fun: Option[String] => Either[ClientError, T], parameter: Parameter) extends QueryParam1[T](name, parameter) {
    override def fromReq: Option[String] => Either[ClientError, Tuple1[T]] =
      s => fun(s) match {
        case Right(value) =>
          Right(Tuple1(value))
        case Left(err) =>
          Left(err)
      }
  }

  private def queryParam: Parameter =
    new Parameter().in("query")

  case class StrParam(name: String) extends Param[String](name, {
    case None =>
      Left(BadRequest(s"Query parameter $name expected"))
    case Some(value) =>
      Right(value)
  }, queryParam.schema(new StringSchema))

  case class StrParamOpt(name: String) extends Param[Option[String]](name,  {
      case None => Right(None)
      case Some(value) => Right(Some(value))
  }, queryParam.schema(new StringSchema))

  case class IntParam(name: String) extends QueryParam1[Int](name, queryParam.schema(new IntegerSchema)) {
    override def fromReq: Option[String] => Either[ClientError, Tuple1[Int]] =
      strToInt
  }

  case class IntParamOpt(name: String) extends QueryParam1[Option[Int]](name, queryParam.schema(new IntegerSchema)) {
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
    PathDefinition(str, FixedPath(str))

  private def strToInt: Option[String] => Either[ClientError, Tuple1[Int]] = {
    case None =>
      Left(BadRequest("Parameter is mandatory"))
    case Some(str) =>
      Try(str.toInt).fold(_ => Left(BadRequest(s"Could not convert $str to Int")), i => Right(Tuple1(i)))
  }

}
