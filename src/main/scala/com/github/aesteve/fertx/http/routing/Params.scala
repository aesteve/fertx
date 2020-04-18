package com.github.aesteve.fertx.http.routing

import com.github.aesteve.fertx.http._
import com.github.aesteve.fertx.utils.TryOrFailWithHttpError
import scala.util.Try


object Params {
  def pathParam[T](name: String, converter: Conversion[String, T]): PathDef[HttpError, T] =
    PathDef.Fragment(s":$name", { request =>
      Try(converter(request.getParam(name)))
        .orHttpError(400, Some(s"Path parameter $name is invalid"))
    })

  def queryParam[T](name: String, converter: Conversion[Option[String], T]): PathDef[HttpError, T] = ???

  object Path {
    def int(name: String): PathDef[HttpError, Int] = pathParam(name, _.toInt)
    def long(name: String): PathDef[HttpError, Long] = pathParam(name, _.toLong)
  }

  object Query {

  }



}
