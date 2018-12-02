package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.dsl.routing.{FinalizedRoute, RouteDefinition}
import com.github.aesteve.fertx.{ResponseType, Response}
import io.vertx.core.http.HttpHeaders
import io.vertx.scala.ext.web.Route

class RouteDefinitionImpl[Path, RequestPayload, MappedPayload, Mime <: ResponseType](
  requestDef: RequestReaderDefinitionImpl[Path, RequestPayload],
  mapper: RequestPayload => MappedPayload,
  produces: Mime
) extends RouteDefinition[MappedPayload, Mime] {

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

  override def mapTuple(f: MappedPayload => Response[Mime]): FinalizedRoute =
    new FinalizedRouteImpl(requestDef, mapper, attachProduces, f)
  override def mapUnit(f: () => Response[Mime]): FinalizedRoute =
    new FinalizedUnitRouteImpl(requestDef, mapper, List(),  f) // doesn't produce anything, since it's "Unit"

  //def flatMap[T](mapping: MappedPayload => Future[T]): RouteDefinition[Path, RequestPayload, T] = ???
  override def produces[NewMime <: ResponseType](mimeType: NewMime): RouteDefinition[MappedPayload, NewMime] =
    new RouteDefinitionImpl[Path, RequestPayload, MappedPayload, NewMime](requestDef, mapper, mimeType)
}
