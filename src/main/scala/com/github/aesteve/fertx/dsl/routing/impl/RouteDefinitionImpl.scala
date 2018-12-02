package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.dsl.path.PathDefinition
import com.github.aesteve.fertx.dsl.routing.{FinalizedRoute, RouteDefinition}
import com.github.aesteve.fertx.request.RequestType
import com.github.aesteve.fertx.response._
import com.github.aesteve.fertx.util.TupleOps.Join
import io.vertx.core.http.{HttpHeaders, HttpMethod}
import io.vertx.scala.ext.web.{Route, RoutingContext}

class RouteDefinitionImpl[Path, In, RequestMime <: RequestType, ResponseMime <: ResponseType](
  val method: HttpMethod,
  val path: PathDefinition[Path],
  val extractor: Extractor[In],
  accepts: RequestMime,
  produces: ResponseMime,
  errorMarshaller: ErrorMarshaller[ResponseMime]
) extends RouteDefinition[In, RequestMime, ResponseMime] {

  val attachAccepts: List[Route => Unit] =
    accepts.representation match {
      case None => List()
      case Some(repr) =>
        List(
          _.consumes(repr)
        )
    }


  val attachProduces: List[Route => Unit] =
    produces.representation match {
      case None => List()
      case Some(repr) =>
        List(
          _.produces(repr),
          _.handler { rc =>
            rc.response.putHeader(HttpHeaders.CONTENT_TYPE.toString, repr)
            rc.next()
          }
        )
    }

  override def lift[C](other: Extractor[C])(implicit join: Join[In, C]): RouteDefinition[join.Out, RequestMime, ResponseMime] =
    new RouteDefinitionImpl(method, path, (extractor & other)(join), accepts, produces, errorMarshaller)

  override def getFromContext: RoutingContext => Either[ClientError, In] =
    extractor.getFromContext

  override def accepts[NewMime <: RequestType](mimeType: NewMime): RouteDefinition[In, NewMime, ResponseMime] =
    new RouteDefinitionImpl(method, path, extractor, mimeType, produces, errorMarshaller)

  override def produces[NewMime <: ResponseType](mimeType: NewMime)(implicit errorMarshaller: ErrorMarshaller[NewMime]): RouteDefinition[In, RequestMime, NewMime] =
    new RouteDefinitionImpl(method, path, extractor, accepts, mimeType, errorMarshaller)

  override def mapTuple(f: In => Response[ResponseMime]): FinalizedRoute =
    new FinalizedRouteImpl(this, attachProduces, f, errorMarshaller)

  //def flatMap[T](mapping: Out => Future[T]): RouteDefinition[Path, In, T] = ???

}
