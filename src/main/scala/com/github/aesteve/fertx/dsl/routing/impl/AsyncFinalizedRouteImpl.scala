package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.media.MimeType
import com.github.aesteve.fertx.response.{ErrorMarshaller, Response}
import io.vertx.lang.scala.VertxExecutionContext
import io.vertx.scala.ext.web.{Route, RoutingContext}

import scala.concurrent.Future

class AsyncFinalizedRouteImpl[Path, In, RequestMime, ResponseMime](
  routeDefinition: RouteDefinitionImpl[Path, In, RequestMime, ResponseMime],
  vertxHandlers: List[Route => Unit],
  responseFinalizer: In => Future[Response[ResponseMime]],
  errorMarshaller: ErrorMarshaller[ResponseMime]
)(implicit requestMime: MimeType[RequestMime], responseMime: MimeType[ResponseMime]) extends AbstractFinalizedRoute(routeDefinition, vertxHandlers, errorMarshaller) {

  override protected def invokeMapper(payload: In, rc: RoutingContext): Unit =
    responseFinalizer(payload).map(_.buildResp(rc.response))(VertxExecutionContext(rc.vertx().getOrCreateContext()))

}
