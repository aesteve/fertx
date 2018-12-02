package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.dsl.routing.FinalizedRoute
import com.github.aesteve.fertx.request.RequestType
import com.github.aesteve.fertx.response.{Response, ResponseType}
import io.vertx.scala.ext.web.handler.BodyHandler
import io.vertx.scala.ext.web.{Route, Router}

class FinalizedUnitRouteImpl[Path, In, Out, RequestMime <: RequestType, ResponseMime <: ResponseType](
  requestReaderDef: RequestReaderDefinitionImpl[Path, In, RequestMime],
  mapper: In => Out,
  vertxHandlers: List[Route => Unit],
  responseFinalizer: () => Response[ResponseMime]
) extends FinalizedRoute {

  override def attachTo(router: Router): Unit = {
    vertxHandlers.foreach { h =>
      h(createRoute(router))
    }
    if (requestReaderDef.extractor.needsBody) {
      createRoute(router).handler(BodyHandler.create())
    }
    router.route(requestReaderDef.method, requestReaderDef.path.toFullPath)
      .handler { rc =>
        requestReaderDef.getFromContext(rc) match {
          case Left(clientError) =>
            rc.response.end(clientError.toString) // FIXME: find proper marshaller & marshall response
          case Right(payload) =>
            mapper(payload)
            responseFinalizer().buildResp(rc.response)
        }
      }
  }

  private def createRoute(router: Router): Route =
    router.routeWithRegex(requestReaderDef.method, requestReaderDef.path.toFullPath)

}
