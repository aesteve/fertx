package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.dsl.routing.FinalizedRoute
import com.github.aesteve.fertx.request.RequestType
import com.github.aesteve.fertx.response.{Response, ResponseType}
import io.vertx.scala.ext.web.handler.BodyHandler
import io.vertx.scala.ext.web.{Route, Router}

class FinalizedRouteImpl[Path, In, Out, BodyMime <: RequestType, ResponseMime <: ResponseType](
  requestReaderDef: RequestReaderDefinitionImpl[Path, In, BodyMime],
  mapper: In => Out,
  vertxHandlers: List[Route => Unit],
  responseFinalizer: Out => Response[ResponseMime]
) extends FinalizedRoute {

  override def attachTo(router: Router): Unit = {
    if (requestReaderDef.extractor.needsBody) {
      createRoute(router).handler(BodyHandler.create())
    }
    vertxHandlers.foreach { h =>
      h(createRoute(router))
    }
    router.routeWithRegex(requestReaderDef.method, requestReaderDef.path.toFullPath)
      .handler { rc =>
        requestReaderDef.getFromContext(rc) match {
          case Left(clientError) =>
            clientError.message match {
              case Some(msg) =>
                rc.response.setStatusCode(clientError.status).end(msg)
              case None => rc.fail(clientError.status)
            }
          case Right(payload) =>
            mapper.andThen(responseFinalizer)(payload).buildResp(rc.response)
        }
      }
  }

  private def createRoute(router: Router): Route =
    router.routeWithRegex(requestReaderDef.method, requestReaderDef.path.toFullPath)
}
