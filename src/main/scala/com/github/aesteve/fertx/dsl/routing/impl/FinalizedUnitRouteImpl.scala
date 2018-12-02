package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.{MimeType, Response}
import com.github.aesteve.fertx.dsl.routing.FinalizedRoute
import io.vertx.scala.ext.web.{Route, Router}

class FinalizedUnitRouteImpl[Path, In, Out, Mime <: MimeType](
  requestReaderDef: RequestReaderDefinitionImpl[Path, In],
  mapper: In => Out,
  vertxHandlers: List[Route => Unit],
  responseFinalizer: () => Response[Mime]
) extends FinalizedRoute {

  override def attachTo(router: Router): Unit = {
    vertxHandlers.foreach { h =>
      h(router.route(requestReaderDef.method, requestReaderDef.path.toFullPath))
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
}
