package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.Response
import com.github.aesteve.fertx.dsl.routing.FinalizedRoute
import io.vertx.scala.ext.web.Router

class FinalizedUnitRouteImpl[Path, In, Out](
  requestReaderDef: RequestReaderDefinition[Path, In],
  mapper: In => Out,
  responseFinalizer: () => Response
) extends FinalizedRoute {

  override def attachTo(router: Router): Unit =
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
