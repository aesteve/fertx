package com.github.aesteve.fertx.dsl.routing

import com.github.aesteve.fertx.dsl.extractors.{Extractor, QueryParamExtractor}
import com.github.aesteve.fertx.dsl.{Param, StrParam, StrParamOpt}
import com.github.aesteve.fertx.request.{RequestType, RequestUnmarshaller, WithSchemaRequestUnmarshaller}
import com.github.aesteve.fertx.response.{BadRequest, ClientError, ErrorMarshaller, ResponseType}
import com.github.aesteve.fertx.util.TupleOps.Join
import com.timeout.docless.schema.JsonSchema

import scala.util.Try

trait RouteDefinition[In, RequestMime <: RequestType, ResponseMime <: ResponseType]
  extends Extractor[In]
  with SealableRoute[In, ResponseMime] {

  def produces[NewMime <: ResponseType](mimeType: NewMime)(implicit errorMarshaller: ErrorMarshaller[NewMime]): RouteDefinition[In, RequestMime, NewMime]

  def accepts[NewMime <: RequestType](mimeType: NewMime): RouteDefinition[In, NewMime, ResponseMime]

  /* The most generic method. Every other should rely on: just lifts data synchronously from RoutingContext using an `Extractor` */
  def lift[C](other: Extractor[C])(implicit join: Join[In, C]): RouteDefinition[join.Out, RequestMime, ResponseMime]

  /* Query parameters */
  def query[P](queryParamExtractor: QueryParamExtractor[P])(implicit join: Join[In, P]): RouteDefinition[join.Out, RequestMime, ResponseMime] =
    lift(queryParamExtractor)(join)

  def query[P](name: String, fun: Option[String] => Either[ClientError, P])(implicit join: Join[In, Tuple1[P]]): RouteDefinition[join.Out, RequestMime, ResponseMime] =
    query(new Param[P](name, fun, param => {
      // FIXME
      //  param.required(false)
      param.as[String]
      param
    }))(join)

  def query(name: String)(implicit join: Join[In, Tuple1[String]]): RouteDefinition[join.Out, RequestMime, ResponseMime] =
    query(StrParam(name))(join)

  def optQuery(name: String)(implicit join: Join[In, Tuple1[Option[String]]]): RouteDefinition[join.Out, RequestMime, ResponseMime] =
    query(StrParamOpt(name))(join)

  def tryQuery[P](name: String, fun: Option[String] => P)(implicit join: Join[In, Tuple1[P]]): RouteDefinition[join.Out, RequestMime, ResponseMime] = {
    val fun2: Option[String] => Either[ClientError, P] =
      s => Try(fun(s)).fold(err => Left(BadRequest(err.getMessage)), p => Right(p))
    query(name, fun2)(join)
  }

  /* Request Body */
  def body[C](implicit schema: JsonSchema[C], unmarshaller: RequestUnmarshaller[RequestMime, C], join: Join[In, Tuple1[C]]): RouteDefinition[join.Out, RequestMime, ResponseMime] =
    lift(new WithSchemaRequestUnmarshaller(unmarshaller, schema))(join)

  /* TODO: deal with headers */

}
