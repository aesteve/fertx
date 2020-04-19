package com.github.aesteve.fertx.http.routing

import com.github.aesteve.fertx.http._
import com.github.aesteve.fertx.utils.TryOrFailWithHttpError
import scala.util.Try


object Params {
  def pathParam[T](name: String, converter: Conversion[String, T]): PathDef[HttpError, T] =
    PathDef.Fragment(s":$name", { request =>
      Try(converter(request.getParam(name)))
        .orHttpError(badRequest(s"Path parameter $name is invalid"))
    })

  def queryParamOpt[T](name: String, converter: Conversion[Option[String], T]): RequestExtractor[HttpError, T] = { request =>
    Try(converter(Option(request.getParam(name))))
      .orHttpError(badRequest(s"Query parameter $name is invalid"))
  }

  def queryParam[T](name: String, converter: Conversion[String, T]): RequestExtractor[HttpError, T] = { request =>
    Option(request.getParam(name)) match {
      case None => Left(badRequest(s"Parametr $name is mandatory"))
      case Some(param) =>
        Try(converter(param))
          .orHttpError(badRequest(s"Query parameter $name is invalid"))
    }
  }

  object Path {
    def int(name: String): PathDef[HttpError, Int] = pathParam(name, _.toInt)
    def long(name: String): PathDef[HttpError, Long] = pathParam(name, _.toLong)
  }

  object Query {
    def int(name: String): RequestExtractor[HttpError, Int] = queryParam(name, _.toInt)
    def long(name: String): RequestExtractor[HttpError, Long] = queryParam(name, _.toLong)

    def intOpt(name: String): RequestExtractor[HttpError, Option[Int]] = queryParamOpt(name, _.map(_.toInt))
    def longOpt(name: String): RequestExtractor[HttpError, Option[Long]] = queryParamOpt(name, _.map(_.toLong))
  }

}
