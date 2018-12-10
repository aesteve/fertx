package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.dsl.routing.FinalizedRoute
import com.github.aesteve.fertx.request.RequestType
import com.github.aesteve.fertx.response.{ErrorMarshaller, ResponseType}
import com.timeout.docless.swagger.{Operation, Path => SwaggerPath}
import io.vertx.core.http.HttpMethod
import io.vertx.scala.ext.web.handler.BodyHandler
import io.vertx.scala.ext.web.{Route, Router, RoutingContext}

abstract class AbstractFinalizedRoute[Path, In, RequestMime <: RequestType, ResponseMime <: ResponseType](
  routeDefinition: RouteDefinitionImpl[Path, In, RequestMime, ResponseMime],
  vertxHandlers: List[Route => Unit],
  errorMarshaller: ErrorMarshaller[ResponseMime]
) extends FinalizedRoute {

  private val operations: Map[HttpMethod, SwaggerPath => Operation => SwaggerPath] = Map(
    HttpMethod.GET -> { _.Get },
    HttpMethod.POST -> { _.Post },
    HttpMethod.PUT -> { _.Put },
    HttpMethod.DELETE -> { _.Delete },
    HttpMethod.OPTIONS -> { _.Options },
    HttpMethod.HEAD -> { _.Head },
    HttpMethod.PATCH -> { _.Patch }
  )

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
            invokeMapper(payload, rc)
        }
      }
  }

  override def openAPIOperation: SwaggerPath =
    operations(routeDefinition.method)(SwaggerPath(routeDefinition.path.toFullPath))(
      routeDefinition.buildOpenAPI(Operation())
    )

  protected def invokeMapper(payload: In, rc: RoutingContext): Unit

  private def createRoute(router: Router): Route =
    router.routeWithRegex(routeDefinition.method, routeDefinition.path.toFullPath)

}
