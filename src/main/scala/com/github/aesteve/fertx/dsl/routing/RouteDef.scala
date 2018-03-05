package com.github.aesteve.fertx.dsl.routing

import com.github.aesteve.fertx.dsl.extractors.{Extractor, HeaderParamExtractor, QueryParamExtractor}
import com.github.aesteve.fertx.{BadRequest, ClientError, OK, Response}
import com.github.aesteve.fertx.dsl.path.PathDef
import com.github.aesteve.fertx.dsl.{IntParam, IntParamOpt, Param, QueryParam1, StrParam, StrParamOpt}
import com.github.aesteve.fertx.util.ApplyConverter
import com.github.aesteve.fertx.util.TupleOps.Join
import io.vertx.core.http.{HttpHeaders, HttpMethod}

import scala.util.Try

class RouteDef[T, R](method: HttpMethod, path: PathDef[T], extractor: Extractor[R]) {

  // Lifting from Request
  def lift[C](other: Extractor[C])(implicit join: Join[R, C]): RouteDef[T, join.Out] =
    new RouteDef[T, join.Out](method, path, (extractor & other)(join))

  def query[P](queryParamExtractor: QueryParamExtractor[P])(implicit join: Join[R, P]): RouteDef[T, join.Out] =
    lift(queryParamExtractor)(join)

  def query[P](name: String, fun: Option[String] => Either[ClientError, P])(implicit join: Join[R, Tuple1[P]]): RouteDef[T, join.Out] =
    query(new Param[P](name, fun))(join)

  def tryQuery[P](name: String, fun: Option[String] => P)(implicit join: Join[R, Tuple1[P]]): RouteDef[T, join.Out] = {
    val fun2: Option[String] => Either[ClientError, P] =
      s => Try(fun(s)).fold(err => Left(BadRequest(err.getMessage)), p => Right(p))
    query(name, fun2)(join)
  }

  def query(name: String)(implicit join: Join[R, Tuple1[String]]): RouteDef[T, join.Out] =
    query(StrParam(name))(join)

  def queryOpt(name: String)(implicit join: Join[R, Tuple1[Option[String]]]): RouteDef[T, join.Out] =
    query(StrParamOpt(name))(join)

  def intQuery(name: String)(implicit join: Join[R, Tuple1[Int]]): RouteDef[T, join.Out] =
    query(IntParam(name))(join)

  def intQueryOpt(name: String)(implicit join: Join[R, Tuple1[Option[Int]]]): RouteDef[T, join.Out] =
    query(IntParamOpt(name))(join)
/*
  def header(name: String)(implicit join: Join[R, String]): RouteDef[T, join.Out] =
    lift(StrHeader(name))(join)
*/

  // Folding to Response
  def foldU(h: () => Response): Unit =
    route.fold(h)

  private def foldTuple(h: R => Response): Unit =
    route.fold(h)

  private def convertApply(implicit hac: ApplyConverter[R]): hac.In => Unit =
    f =>
      foldTuple(hac(f))

  // Mapping to a Route
  private def route: FRoute[T, R, R] =
    new FRoute[T, R, R](this, this.extractor)

}

object RouteDef {

  abstract class CanFold[C] {
    def fold(f: C): Unit
  }

  implicit def addRouteFolding[AnyRef, R](routeDef: RouteDef[AnyRef, R])(implicit hac: ApplyConverter[R]): CanFold[hac.In] =
    (f: hac.In) => routeDef(f)

  implicit def addRouteApply[T, R](routeDef: RouteDef[T, R])(implicit hac: ApplyConverter[R]): hac.In ⇒ Unit =
    routeDef.convertApply(hac)

}

