package com.github.aesteve.fertx.dsl.routing

import com.github.aesteve.fertx.dsl.{Param, StrParam, StrParamOpt}
import com.github.aesteve.fertx.{BadRequest, ClientError, TextPlain}
import com.github.aesteve.fertx.dsl.extractors.{Extractor, QueryParamExtractor}
import com.github.aesteve.fertx.util.TupleOps.Join

import scala.util.Try

trait RequestReaderDefinition[Path, RequestPayload]
  extends Extractor[RequestPayload] with RouteDefinition[RequestPayload, TextPlain] {

  /* The most generic method. Every other should rely on: just lifts data synchronously from RoutingContext using an `Extractor` */
  def lift[C](other: Extractor[C])(implicit join: Join[RequestPayload, C]): RequestReaderDefinition[Path, join.Out]

  /* Query parameters */
  def query[P](queryParamExtractor: QueryParamExtractor[P])(implicit join: Join[RequestPayload, P]): RequestReaderDefinition[Path, join.Out] =
    lift(queryParamExtractor)(join)

  def query[P](name: String, fun: Option[String] => Either[ClientError, P])(implicit join: Join[RequestPayload, Tuple1[P]]): RequestReaderDefinition[Path, join.Out] =
    query(new Param[P](name, fun))(join)

  def query(name: String)(implicit join: Join[RequestPayload, Tuple1[String]]): RequestReaderDefinition[Path, join.Out] =
    query(StrParam(name))(join)

  def optQuery(name: String)(implicit join: Join[RequestPayload, Tuple1[Option[String]]]): RequestReaderDefinition[Path, join.Out] =
    query(StrParamOpt(name))(join)

  def tryQuery[P](name: String, fun: Option[String] => P)(implicit join: Join[RequestPayload, Tuple1[P]]): RequestReaderDefinition[Path, join.Out] = {
    val fun2: Option[String] => Either[ClientError, P] =
      s => Try(fun(s)).fold(err => Left(BadRequest(err.getMessage)), p => Right(p))
    query(name, fun2)(join)
  }

  /* TODO: deal with body */

  /* TODO: deal with headers */
}
