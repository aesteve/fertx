package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.Response
import com.github.aesteve.fertx.dsl.routing.FinalizedRoute
import io.vertx.scala.ext.web.Router

class FinalizedRouteImpl[Path, In, Out](
  requestReaderDef: RequestReaderDefinition[Path, In],
  mapper: In => Out,
  responseFinalizer: Out => Response
) extends FinalizedRoute {

  override def attachTo(router: Router): Unit =
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
