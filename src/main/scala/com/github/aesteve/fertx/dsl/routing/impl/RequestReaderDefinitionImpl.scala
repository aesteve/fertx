package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.dsl.path.PathDefinition
import com.github.aesteve.fertx.dsl.routing.{FinalizedRoute, RequestReaderDefinition, RouteDefinition}
import com.github.aesteve.fertx.request.RequestType
import com.github.aesteve.fertx.util.TupleOps.Join
import com.github.aesteve.fertx.response
import com.github.aesteve.fertx.response._
import io.vertx.core.http.HttpMethod
import io.vertx.scala.ext.web.RoutingContext

class RequestReaderDefinitionImpl[Path, RequestPayload, CurrentRequestType <: RequestType](
  val method: HttpMethod,
  val path: PathDefinition[Path],
  val extractor: Extractor[RequestPayload],
  val accepts: CurrentRequestType
) extends RequestReaderDefinition[Path, RequestPayload, CurrentRequestType] {

  // Lifting from Request
  override def lift[C](other: Extractor[C])(implicit join: Join[RequestPayload, C]): RequestReaderDefinition[Path, join.Out, CurrentRequestType] =
    new RequestReaderDefinitionImpl(method, path, (extractor & other)(join), accepts)

/*

  def intQuery(name: String)(implicit join: Join[RequestPayload, Tuple1[Int]]): RequestReaderDefinitionImpl[Path, join.Out] =
    query(IntParam(name))(join)

  def intQueryOpt(name: String)(implicit join: Join[RequestPayload, Tuple1[Option[Int]]]): RequestReaderDefinitionImpl[Path, join.Out] =

    query(IntParamOpt(name))(join)

  def header(name: String)(implicit join: Join[R, String]): RouteDef[T, join.Out] =
    lift(StrHeader(name))(join)
*/

  override def getFromContext: RoutingContext => Either[ClientError, RequestPayload] =
    extractor.getFromContext

  override def mapTuple(f: RequestPayload => Response[response.NoContent]): FinalizedRoute =
    route.mapTuple(f)

  override def accepts[NewMime <: RequestType](mimeType: NewMime): RequestReaderDefinition[Path, RequestPayload, NewMime] =
    new RequestReaderDefinitionImpl(method, path, extractor, mimeType)

  override def produces[NewMime <: ResponseType](mimeType: NewMime)(implicit errorMarshaller: ErrorMarshaller[NewMime]): RouteDefinition[RequestPayload, CurrentRequestType, NewMime] =
    route.produces(mimeType)

  // Mapping to a Route
  private[fertx] def route: RouteDefinition[RequestPayload, CurrentRequestType, response.NoContent] =
    new RouteDefinitionImpl(this, identity[RequestPayload], accepts, ResponseType.NO_CONTENT, NoContentErrorMarshaller)

}

