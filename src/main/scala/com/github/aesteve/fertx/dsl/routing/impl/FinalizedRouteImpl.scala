package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.dsl.routing.FinalizedRoute
import com.github.aesteve.fertx.request.RequestType
import com.github.aesteve.fertx.response.{ErrorMarshaller, Response, ResponseType}
import io.vertx.scala.ext.web.handler.BodyHandler
import io.vertx.scala.ext.web.{Route, Router}

class FinalizedRouteImpl[Path, In, Out, BodyMime <: RequestType, ResponseMime <: ResponseType](
  requestReaderDef: RequestReaderDefinitionImpl[Path, In, BodyMime],
  mapper: In => Out,
  vertxHandlers: List[Route => Unit],
  responseFinalizer: Out => Response[ResponseMime],
  errorMarshaller: ErrorMarshaller[ResponseMime]
) extends FinalizedRoute {

  override def attachTo(router: Router): Unit = {
    if (requestReaderDef.extractor.needsBody) {
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
        requestReaderDef.getFromContext(rc) match {
          case Left(clientError) =>
            errorMarshaller.handle(rc.response, clientError)
          case Right(payload) =>
            mapper.andThen(responseFinalizer)(payload).buildResp(rc.response)
        }
      }
  }

  private def createRoute(router: Router): Route =
    router.routeWithRegex(requestReaderDef.method, requestReaderDef.path.toFullPath)
}
