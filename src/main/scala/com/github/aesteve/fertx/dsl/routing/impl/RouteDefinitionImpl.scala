package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.dsl.path.PathDefinition
import com.github.aesteve.fertx.dsl.routing.{FinalizedRoute, RouteDefinition}
import com.github.aesteve.fertx.media.MimeType
import com.github.aesteve.fertx.response._
import com.github.aesteve.fertx.util.TupleOps.Join
import io.swagger.v3.oas.models.Operation
import io.vertx.core.http.{HttpHeaders, HttpMethod}
import io.vertx.scala.ext.web.{Route, RoutingContext}

import scala.concurrent.Future

class RouteDefinitionImpl[Path, In, RequestMime, ResponseMime](
  val method: HttpMethod,
  val path: PathDefinition[Path],
  val extractor: Extractor[In],
  errorMarshaller: ErrorMarshaller[ResponseMime]
)(implicit requestMime: MimeType[RequestMime], responseMime: MimeType[ResponseMime])
  extends RouteDefinition[In, RequestMime, ResponseMime] {

  val attachAccepts: List[Route => Unit] =
    requestMime.representation match {
      case None => List()
      case Some(repr) =>
        List(
          _.consumes(repr)
        )
    }


  val attachProduces: List[Route => Unit] =
    responseMime.representation match {
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
    new RouteDefinitionImpl(method, path, (extractor & other)(join), errorMarshaller)

  override def getFromContext: RoutingContext => Either[ClientError, In] =
    extractor.getFromContext

  override def accepts[NewMime](implicit mime: MimeType[NewMime]): RouteDefinition[In, NewMime, ResponseMime] =
    new RouteDefinitionImpl(method, path, extractor, errorMarshaller)(mime, responseMime)

  override def produces[NewMime](implicit mime: MimeType[NewMime], errorMarshaller: ErrorMarshaller[NewMime]): RouteDefinition[In, RequestMime, NewMime] =
    new RouteDefinitionImpl(method, path, extractor, errorMarshaller)(requestMime, mime)

  override def mapTuple(f: In => Response[ResponseMime]): FinalizedRoute =
    new FinalizedRouteImpl(this, attachProduces, f, errorMarshaller)

  override def flatMapTuple(f: In => Future[Response[ResponseMime]]): FinalizedRoute =
    new AsyncFinalizedRouteImpl(this, attachProduces, f, errorMarshaller)

  override def buildOpenApi(operation: Operation): Operation =
    extractor.buildOpenApi(operation)
  
}
