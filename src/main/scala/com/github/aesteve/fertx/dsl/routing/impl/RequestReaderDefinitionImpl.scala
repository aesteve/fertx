package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.dsl.path.PathDefinition
import com.github.aesteve.fertx.dsl.routing.{FinalizedRoute, RequestReaderDefinition, RouteDefinition}
import com.github.aesteve.fertx.util.TupleOps.Join
import com.github.aesteve.fertx._
import io.vertx.core.http.HttpMethod
import io.vertx.scala.ext.web.RoutingContext

class RequestReaderDefinitionImpl[Path, RequestPayload]
  (val method: HttpMethod, val path: PathDefinition[Path], extractor: Extractor[RequestPayload])
  extends RequestReaderDefinition[Path, RequestPayload] {

  // Lifting from Request
  override def lift[C](other: Extractor[C])(implicit join: Join[RequestPayload, C]): RequestReaderDefinition[Path, join.Out] =
    new RequestReaderDefinitionImpl[Path, join.Out](method, path, (extractor & other)(join))

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

  override def mapTuple(f: RequestPayload => Response[NoContent]): FinalizedRoute =
    route.mapTuple(f)

  override def mapUnit(f: () => Response[NoContent]): FinalizedRoute =
    route.mapUnit(f)

  override def produces[NewMime <: MimeType](mimeType: NewMime): RouteDefinition[RequestPayload, NewMime] =
    new RouteDefinitionImpl[Path, RequestPayload, RequestPayload, NewMime](this, identity, mimeType)

  // Mapping to a Route
  private def route: RouteDefinition[RequestPayload, NoContent] =
    new RouteDefinitionImpl[Path, RequestPayload, RequestPayload, NoContent](this, identity, MimeType.NO_CONTENT)


}

