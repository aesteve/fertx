package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.{MimeType, Response}
import com.github.aesteve.fertx.dsl.routing.FinalizedRoute
import io.vertx.scala.ext.web.{Route, Router}

class FinalizedRouteImpl[Path, In, Out, Mime <: MimeType](
  requestReaderDef: RequestReaderDefinitionImpl[Path, In],
  mapper: In => Out,
  vertxHandlers: List[Route => Unit],
  responseFinalizer: Out => Response[Mime]
) extends FinalizedRoute {

  override def attachTo(router: Router): Unit = {
    vertxHandlers.foreach { h =>
      h(router.routeWithRegex(requestReaderDef.method, requestReaderDef.path.toFullPath))
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
}
