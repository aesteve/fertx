package com.github.aesteve.fertx.dsl.routing

 import com.github.aesteve.fertx.dsl.extractors.Extractor
 import com.github.aesteve.fertx.dsl.query._
 import com.github.aesteve.fertx.media.MimeType
 import com.github.aesteve.fertx.request.RequestUnmarshaller
 import com.github.aesteve.fertx.response.ErrorMarshaller
 import com.github.aesteve.fertx.util.TupleOps.Join

abstract class RouteDefinition[In, RequestMime, ResponseMime]
  extends Extractor[In]
  with SealableRoute[In, ResponseMime] {

  def produces[NewMime](implicit mime: MimeType[NewMime], errorMarshaller: ErrorMarshaller[NewMime]): RouteDefinition[In, RequestMime, NewMime]

  def accepts[NewMime](implicit mime: MimeType[NewMime]): RouteDefinition[In, NewMime, ResponseMime]

  /* The most generic method. Every other should rely on: just lifts data synchronously from RoutingContext using an `Extractor` */
  def lift[C](other: Extractor[C])(implicit join: Join[In, C]): RouteDefinition[join.Out, RequestMime, ResponseMime]

  /* Query parameters */
  def query[P](queryParam: QueryParam[P])(implicit join: Join[In, Tuple1[P]]): RouteDefinition[join.Out, RequestMime, ResponseMime] =
    lift(queryParam)(join)

  def query[P](queryParam: OptionQueryParam[P])(implicit join: Join[In, Tuple1[Option[P]]]): RouteDefinition[join.Out, RequestMime, ResponseMime] =
    lift(queryParam)(join)

  /* Request Body */
  def body[C](implicit unmarshaller: RequestUnmarshaller[RequestMime, C], join: Join[In, Tuple1[C]]): RouteDefinition[join.Out, RequestMime, ResponseMime] =
    lift(unmarshaller)(join)

  /* TODO: deal with headers */

}
