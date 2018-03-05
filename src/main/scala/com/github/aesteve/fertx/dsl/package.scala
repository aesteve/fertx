package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl.extractors.QueryParamExtractor
import com.github.aesteve.fertx.dsl.path.{PathFragDef, _}
import com.github.aesteve.fertx.dsl.routing.RouteDef
import io.vertx.core.http.HttpMethod

import scala.util.Try

package object dsl {

  type PathFragDef0 = PathFragDef[Unit]
  type PathFragDef1[T] = PathFragDef[Tuple1[T]]
  type QueryParam1[T] = QueryParamExtractor[Tuple1[T]]

  def GET[T](pathDef: PathDef[T]): RouteDef[T, T] =
    new RouteDef[T, T](HttpMethod.GET, pathDef, pathDef.extractor)

  def POST[T](pathDef: PathDef[T]): RouteDef[T, T] =
    new RouteDef[T, T](HttpMethod.POST, pathDef, pathDef.extractor)


  object * extends PathFragDef0 {
    override def toRegex: String = "*"

    override def fromParam: Option[String] => Either[ClientError, Unit] = _ => Right(())
  }

  case class StrPath(str: String) extends PathFragDef0 {

    override def toRegex: String =
      str

    override def fromParam: Option[String] => Either[ClientError, Unit] =
      _ => Right(())

  }

  object IntPath extends PathFragDef1[Int] {

    override def toRegex: String =
      "(\\d+)"

    override def fromParam: Option[String] => Either[ClientError, Tuple1[Int]] =
      strToInt

  }


  // abstract class Param[T](name: String) extends QueryParam1[T](name)

  class Param[T](name: String, fun: Option[String] => Either[ClientError, T]) extends QueryParam1[T](name) {
    override def fromReq: Option[String] => Either[ClientError, Tuple1[T]] =
      s => fun(s) match {
        case Right(value) => Right(Tuple1(value))
        case Left(err) => Left(err)
      }
  }

  case class StrParam(name: String) extends Param[String](name, {
    case None => Left(BadRequest(s"Query parameter $name expected"))
    case Some(value) => Right(value)
  })

  case class StrParamOpt(name: String) extends Param[Option[String]](name,  {
      case None => Right(None)
      case Some(value) => Right(Some(value))
  })

  case class IntParam(name: String) extends QueryParam1[Int](name) {
    override def fromReq: Option[String] => Either[ClientError, Tuple1[Int]] =
      strToInt
  }

  case class IntParamOpt(name: String) extends QueryParam1[Option[Int]](name) {
    override def fromReq: Option[String] => Either[ClientError, Tuple1[Option[Int]]] = {
      case None =>
        Right(Tuple1(None))
      case Some(str) =>
        Try(str.toInt).fold(_ => Left(BadRequest(s"Could not convert $str to Int")), i => Right(Tuple1(Some(i))))
    }

  }


  implicit def pathFragDefFromStr(str: String): StrPath =
    StrPath(str)

  implicit def pathDefFromStr(str: String): PathDef[Unit] =
    PathDef(str, StrPath(str).at(0))

  private def strToInt: Option[String] => Either[ClientError, Tuple1[Int]] = {
    case None =>
      Left(BadRequest("Parameter is mandatory"))
    case Some(str) =>
      Try(str.toInt).fold(_ => Left(BadRequest(s"Could not convert $str to Int")), i => Right(Tuple1(i)))
  }

}
