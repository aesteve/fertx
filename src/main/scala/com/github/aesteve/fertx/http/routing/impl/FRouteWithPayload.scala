package com.github.aesteve.fertx.http.routing.impl

import com.github.aesteve.fertx.http._
import com.github.aesteve.fertx.http.routing.{FRoute, PathDef}
import com.github.aesteve.fertx.utils.{CanJoin, TryOrFailWithHttpError}
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.{Route, Router}

import scala.util.Try

class FRouteWithPayload[Error <: HttpError, Payload](vertxSupplier: Router => Route, handler: RequestExtractor[Error, Payload]) extends FRoute[Error, Payload] {
  def flatMap[Left <: HttpError, T](f: Payload => Either[Left, T]): FRoute[Error | Left, T] = {
    new FRouteWithPayload(vertxSupplier, { request =>
      handler(request) match {
        case Left(error) => Left(error)
        case Right(payload) => f(payload)
      }
    })
  }

  def seal(errorHandler: Marshaller[Error], writer: Marshaller[Payload]): Request => Response = { request =>
    handler(request) match {
      case Left(error) => errorHandler(error)
      case Right(payload) => writer(payload)
    }
  }
  
  def join[E <: HttpError, P](other: RequestExtractor[E, P])(using joiner: CanJoin[Payload, P]): FRouteWithPayload[Error | E, joiner.Joined] = {
    new FRouteWithPayload(vertxSupplier, { request =>
      handler(request) match {
        case Left(error) => Left(error)
        case Right(payload) => other(request) match {
          case Left(error) => Left(error)
          case Right(otherPayload) => Right(joiner.join(payload, otherPayload))
        }
      }
    })
  }
  
  def join[P](fromRequest: Request => String, converter: Conversion[String, P], msg: Option[String])(using joiner: CanJoin[Payload, P]): FRouteWithPayload[Error | HttpError, joiner.Joined] = 
    join({ request =>
      Try(converter(fromRequest(request)))
        .orHttpError(400, msg)
    })
  
  def query[P](name: String, converter: Conversion[String, P])(using joiner: CanJoin[Payload, P]) = join(_.getParam(name), converter, Some(s"$name query parameter is invalid"))

}

