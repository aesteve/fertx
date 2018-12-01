package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.dsl.extractors.{Extractor, QueryParamExtractor}
import com.github.aesteve.fertx.dsl.path.PathDefinition
import com.github.aesteve.fertx.dsl.routing.{FinalizedRoute, RouteDefinition}
import com.github.aesteve.fertx.dsl.{IntParam, IntParamOpt, Param, StrParam, StrParamOpt}
import com.github.aesteve.fertx.util.TupleOps.Join
import com.github.aesteve.fertx.{BadRequest, ClientError, Response}
import io.vertx.core.http.HttpMethod
import io.vertx.scala.ext.web.RoutingContext

import scala.util.Try

class RequestReaderDefinition[Path, RequestPayload]
  (val method: HttpMethod, val path: PathDefinition[Path], extractor: Extractor[RequestPayload])
  extends Extractor[RequestPayload] with RouteDefinition[RequestPayload] {

  // Lifting from Request
  def lift[C](other: Extractor[C])(implicit join: Join[RequestPayload, C]): RequestReaderDefinition[Path, join.Out] =
    new RequestReaderDefinition[Path, join.Out](method, path, (extractor & other)(join))

  def query[P](queryParamExtractor: QueryParamExtractor[P])(implicit join: Join[RequestPayload, P]): RequestReaderDefinition[Path, join.Out] =
    lift(queryParamExtractor)(join)

  def query[P](name: String, fun: Option[String] => Either[ClientError, P])(implicit join: Join[RequestPayload, Tuple1[P]]): RequestReaderDefinition[Path, join.Out] =
    query(new Param[P](name, fun))(join)

  def tryQuery[P](name: String, fun: Option[String] => P)(implicit join: Join[RequestPayload, Tuple1[P]]): RequestReaderDefinition[Path, join.Out] = {
    val fun2: Option[String] => Either[ClientError, P] =
      s => Try(fun(s)).fold(err => Left(BadRequest(err.getMessage)), p => Right(p))
    query(name, fun2)(join)
  }

  def query(name: String)(implicit join: Join[RequestPayload, Tuple1[String]]): RequestReaderDefinition[Path, join.Out] =
    query(StrParam(name))(join)

  def queryOpt(name: String)(implicit join: Join[RequestPayload, Tuple1[Option[String]]]): RequestReaderDefinition[Path, join.Out] =
    query(StrParamOpt(name))(join)

  def intQuery(name: String)(implicit join: Join[RequestPayload, Tuple1[Int]]): RequestReaderDefinition[Path, join.Out] =
    query(IntParam(name))(join)

  def intQueryOpt(name: String)(implicit join: Join[RequestPayload, Tuple1[Option[Int]]]): RequestReaderDefinition[Path, join.Out] =
    query(IntParamOpt(name))(join)
/*
  def header(name: String)(implicit join: Join[R, String]): RouteDef[T, join.Out] =
    lift(StrHeader(name))(join)
*/

  override def getFromContext: RoutingContext => Either[ClientError, RequestPayload] =
    extractor.getFromContext

  override def mapTuple(f: RequestPayload => Response): FinalizedRoute =
    route.mapTuple(f)

  override def mapUnit(f: () => Response): FinalizedRoute =
    route.mapUnit(f)

  // Mapping to a Route
  private def route: RouteDefinitionImpl[Path, RequestPayload, RequestPayload] =
    new RouteDefinitionImpl[Path, RequestPayload, RequestPayload](this, identity)

}

