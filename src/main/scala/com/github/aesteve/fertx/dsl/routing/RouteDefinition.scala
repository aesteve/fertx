package com.github.aesteve.fertx.dsl.routing

import com.github.aesteve.fertx.dsl.extractors.{Extractor, QueryParamExtractor}
import com.github.aesteve.fertx.dsl.{Param, StrParam, StrParamOpt}
import com.github.aesteve.fertx.media.MimeType
import com.github.aesteve.fertx.request.RequestUnmarshaller
import com.github.aesteve.fertx.response.{ClientError, ErrorMarshaller}
import com.github.aesteve.fertx.util.TupleOps.Join
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.QueryParameter

abstract class RouteDefinition[In, RequestMime, ResponseMime]
  extends Extractor[In]
  with SealableRoute[In, ResponseMime] {

  def produces[NewMime](implicit mime: MimeType[NewMime], errorMarshaller: ErrorMarshaller[NewMime]): RouteDefinition[In, RequestMime, NewMime]

  def accepts[NewMime](implicit mime: MimeType[NewMime]): RouteDefinition[In, NewMime, ResponseMime]

  /* The most generic method. Every other should rely on: just lifts data synchronously from RoutingContext using an `Extractor` */
  def lift[C](other: Extractor[C])(implicit join: Join[In, C]): RouteDefinition[join.Out, RequestMime, ResponseMime]

  /* Query parameters */
  def query[P](queryParamExtractor: QueryParamExtractor[P])(implicit join: Join[In, P]): RouteDefinition[join.Out, RequestMime, ResponseMime] =
    lift(queryParamExtractor)(join)

  def query[P](name: String, fun: Option[String] => Either[ClientError, P], schema: Schema[P])(implicit join: Join[In, Tuple1[P]]): RouteDefinition[join.Out, RequestMime, ResponseMime] =
    query(new Param[P](name, fun, new QueryParameter().schema(schema)))(join)

  def query(name: String)(implicit join: Join[In, Tuple1[String]]): RouteDefinition[join.Out, RequestMime, ResponseMime] =
    query(StrParam(name))(join)

  def optQuery(name: String)(implicit join: Join[In, Tuple1[Option[String]]]): RouteDefinition[join.Out, RequestMime, ResponseMime] =
    query(StrParamOpt(name))(join)

  /* Request Body */
  def body[C](implicit unmarshaller: RequestUnmarshaller[RequestMime, C], join: Join[In, Tuple1[C]]): RouteDefinition[join.Out, RequestMime, ResponseMime] =
    lift(unmarshaller)(join)

  /* TODO: deal with headers */

}
