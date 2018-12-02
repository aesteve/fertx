package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.dsl.routing.{FinalizedRoute, RouteDefinition}
import com.github.aesteve.fertx.{MimeType, Response}
import io.vertx.core.http.HttpHeaders
import io.vertx.scala.ext.web.Route

class RouteDefinitionImpl[Path, RequestPayload, MappedPayload, Mime <: MimeType](
  requestDef: RequestReaderDefinition[Path, RequestPayload],
  mapper: RequestPayload => MappedPayload,
  produces: Mime
) extends RouteDefinition[MappedPayload, Mime] {

  val attachProduces: List[Route => Unit] =
    List(
      _.produces(produces.representation),
      _.handler { rc =>
        rc.response.putHeader(HttpHeaders.CONTENT_TYPE.toString, produces.representation)
        rc.next()
      }
    )

  override def mapTuple(f: MappedPayload => Response): FinalizedRoute =
    new FinalizedRouteImpl(requestDef, mapper, attachProduces, f)
  override def mapUnit(f: () => Response): FinalizedRoute =
    new FinalizedUnitRouteImpl(requestDef, mapper, List(),  f) // doesn't produce anything, since it's "Unit"

  //def flatMap[T](mapping: MappedPayload => Future[T]): RouteDefinition[Path, RequestPayload, T] = ???
  override def produces[NewMime <: MimeType](mimeType: NewMime): RouteDefinition[MappedPayload, NewMime] =
    new RouteDefinitionImpl[Path, RequestPayload, MappedPayload, NewMime](requestDef, mapper, mimeType)
}
