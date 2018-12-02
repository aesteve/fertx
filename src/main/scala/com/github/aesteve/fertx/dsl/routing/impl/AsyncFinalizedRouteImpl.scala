package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.request.RequestType
import com.github.aesteve.fertx.response.{ErrorMarshaller, Response, ResponseType}
import io.vertx.lang.scala.VertxExecutionContext
import io.vertx.scala.ext.web.{Route, RoutingContext}

import scala.concurrent.Future

class AsyncFinalizedRouteImpl[Path, In, RequestMime <: RequestType, ResponseMime <: ResponseType](
  routeDefinition: RouteDefinitionImpl[Path, In, RequestMime, ResponseMime],
  vertxHandlers: List[Route => Unit],
  responseFinalizer: In => Future[Response[ResponseMime]],
  errorMarshaller: ErrorMarshaller[ResponseMime]
) extends AbstractFinalizedRoute(routeDefinition, vertxHandlers, errorMarshaller) {

  override protected def invokeMapper(payload: In, rc: RoutingContext): Unit =
    responseFinalizer(payload).map(_.buildResp(rc.response))(VertxExecutionContext(rc.vertx().getOrCreateContext()))

}
