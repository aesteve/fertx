package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.dsl.routing.{FinalizedRoute, RouteDefinition}
import com.github.aesteve.fertx.request.RequestType
import com.github.aesteve.fertx.response.{Response, ResponseType}
import io.vertx.core.http.HttpHeaders
import io.vertx.scala.ext.web.Route

class RouteDefinitionImpl[Path, RequestPayload, MappedPayload, RequestMime <: RequestType, ResponseMime <: ResponseType](
  requestDef: RequestReaderDefinitionImpl[Path, RequestPayload, RequestMime],
  mapper: RequestPayload => MappedPayload,
  accepts: RequestMime,
  produces: ResponseMime
) extends RouteDefinition[MappedPayload, RequestMime, ResponseMime] {

  val attachAccepts: List[Route => Unit] =
    accepts.representation match {
      case None => List()
      case Some(repr) =>
        List(
          _.consumes(repr)
        )
    }


  val attachProduces: List[Route => Unit] =
    produces.representation match {
      case None => List()
      case Some(repr) =>
        List(
          _.produces(repr),
          _.handler { rc =>
            rc.response.putHeader(HttpHeaders.CONTENT_TYPE.toString, repr)
            rc.next()
          }
        )
    }

  override def mapTuple(f: MappedPayload => Response[ResponseMime]): FinalizedRoute =
    new FinalizedRouteImpl(requestDef, mapper, attachProduces, f)
  override def mapUnit(f: () => Response[ResponseMime]): FinalizedRoute =
    new FinalizedUnitRouteImpl(requestDef, mapper, List(),  f) // doesn't produce anything, since it's "Unit"

  //def flatMap[T](mapping: MappedPayload => Future[T]): RouteDefinition[Path, RequestPayload, T] = ???
  override def produces[NewMime <: ResponseType](mimeType: NewMime): RouteDefinition[MappedPayload, RequestMime, NewMime] =
    new RouteDefinitionImpl(requestDef, mapper, accepts, mimeType)

}
