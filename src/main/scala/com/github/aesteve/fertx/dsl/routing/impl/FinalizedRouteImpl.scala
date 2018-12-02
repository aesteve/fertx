package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.dsl.routing.FinalizedRoute
import com.github.aesteve.fertx.request.RequestType
import com.github.aesteve.fertx.response.{ErrorMarshaller, Response, ResponseType}
import io.vertx.scala.ext.web.handler.BodyHandler
import io.vertx.scala.ext.web.{Route, Router}

class FinalizedRouteImpl[Path, In, RequestMime <: RequestType, ResponseMime <: ResponseType](
  routeDefinition: RouteDefinitionImpl[Path, In, RequestMime, ResponseMime],
  vertxHandlers: List[Route => Unit],
  responseFinalizer: In => Response[ResponseMime],
  errorMarshaller: ErrorMarshaller[ResponseMime]
) extends FinalizedRoute {

  override def attachTo(router: Router): Unit = {
    if (routeDefinition.extractor.needsBody) {
      createRoute(router).handler(BodyHandler.create())
    }
    vertxHandlers.foreach { h =>
      h(createRoute(router))
    }
    createRoute(router).failureHandler { rc =>
      errorMarshaller.handle(rc.response, rc.failure)
    }
    createRoute(router)
      .handler { rc =>
        routeDefinition.getFromContext(rc) match {
          case Left(clientError) =>
            errorMarshaller.handle(rc.response, clientError)
          case Right(payload) =>
            responseFinalizer(payload).buildResp(rc.response)
        }
      }
  }

  private def createRoute(router: Router): Route =
    router.routeWithRegex(routeDefinition.method, routeDefinition.path.toFullPath)
}
