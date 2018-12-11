package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.media.MimeType
import com.github.aesteve.fertx.response.{ErrorMarshaller, Response}
import io.vertx.scala.ext.web.{Route, RoutingContext}

class FinalizedRouteImpl[Path, In, RequestMime, ResponseMime](
  routeDefinition: RouteDefinitionImpl[Path, In, RequestMime, ResponseMime],
  vertxHandlers: List[Route => Unit],
  responseFinalizer: In => Response[ResponseMime],
  errorMarshaller: ErrorMarshaller[ResponseMime]
)(implicit requestMime: MimeType[RequestMime], responseMime: MimeType[ResponseMime])
  extends AbstractFinalizedRoute[Path, In, RequestMime, ResponseMime](routeDefinition, vertxHandlers, errorMarshaller) {

  override protected def invokeMapper(payload: In, rc: RoutingContext): Unit =
    responseFinalizer(payload).buildResp(rc.response)

}
