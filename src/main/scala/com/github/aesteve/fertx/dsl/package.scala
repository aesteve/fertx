package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl.extractors.QueryParamExtractor
import com.github.aesteve.fertx.dsl.path.{PathFragmentDefinition, _}
import com.github.aesteve.fertx.dsl.routing.RouteDefinition
import com.github.aesteve.fertx.dsl.routing.impl.RouteDefinitionImpl
import com.github.aesteve.fertx.media._
import com.github.aesteve.fertx.response.{BadRequest, ClientError, UnitErrorMarshaller}
import io.swagger.v3.oas.models.media.{IntegerSchema, StringSchema}
import io.swagger.v3.oas.models.parameters.{Parameter, PathParameter}
import io.vertx.core.http.HttpMethod

import scala.util.Try

package object dsl {

  /* Request definition */
  def request[T](method: HttpMethod, pathDef: PathDefinition[T]): RouteDefinition[T, Unit, Unit] =
    new RouteDefinitionImpl(method, pathDef, pathDef.extractor, UnitErrorMarshaller)

  def GET[T](pathDef: PathDefinition[T]): RouteDefinition[T, Unit, Unit] =
    request(HttpMethod.GET, pathDef)

  def POST[T](pathDef: PathDefinition[T]): RouteDefinition[T, Unit, Unit] =
    request(HttpMethod.POST, pathDef)


  /* Path creation */
  object * extends UnitPathFragment("*") with FinalPathFragmentDefinition[Unit]
  case class FixedPath(str: String) extends UnitPathFragment(str)
  private case class StrPath(name: String) extends CapturesPathFragment[Tuple1[String]](new PathParameter().name(name).schema(new StringSchema)) {
    override def fromParam: Option[String] => Either[ClientError, Tuple1[String]] = opt =>
      Right(Tuple1(opt.get))
  }
  implicit val shortMapper:   Tuple1[String] => Short = _._1.toShort
  implicit val intMapper:     Tuple1[String] => Int = _._1.toInt
  implicit val longMapper:    Tuple1[String] => Long = _._1.toLong
  implicit val doubleMapper:  Tuple1[String] => Double = _._1.toDouble
  implicit val floatMapper:   Tuple1[String] => Float = _._1.toFloat
  implicit val booleanMapper: Tuple1[String] => Boolean = _._1.toBoolean

  implicit def pathFragDefFromStr(str: String): FixedPath =
    FixedPath(str)

  implicit def pathFragDefFromSymbol(symbol: Symbol): PathFragmentDefinition[Tuple1[String]] =
    StrPath(symbol.name)

  implicit def pathDefFromSymbol(symbol: Symbol): NonFinalPathDefinition[Tuple1[String]] =
    new NonFinalPathDefinition(symbol.name, StrPath(symbol.name))

  implicit def pathDefFromStr(str: String): NonFinalPathDefinition[Unit] =
    new NonFinalPathDefinition(str, FixedPath(str))


  /* Query parameters */
  private def queryParam: Parameter =
    new Parameter().in("query")

  class MandatoryQueryParam[T](parameter: Parameter, mapper: String => T) extends QueryParamExtractor[Tuple1[T]](parameter.required(true)) {
    override def fromReq: Option[String] => Either[ClientError, Tuple1[T]] = {
      case None => Left(BadRequest(s"Query parameter ${parameter.getName} is missing"))
      case Some(value) => Try(mapper(value))
        .fold(_ => Left(BadRequest(s"Cannot read parameter ${parameter.getName}")), mapped => Right(Tuple1(mapped)))
    }
  }

  class OptionalQueryParam[T](parameter: Parameter, mapper: String => T) extends QueryParamExtractor[Tuple1[Option[T]]](parameter.required(false)) {
    override def fromReq: Option[String] => Either[ClientError, Tuple1[Option[T]]] = {
      case None => Right(Tuple1(None))
      case Some(value) => Try(mapper(value))
        .fold(_ => Left(BadRequest(s"Cannot read parameter ${parameter.getName}")), mapped => Right(Tuple1(Some(mapped))))
    }
  }

  case class StrParam(name: String) extends MandatoryQueryParam[String](queryParam.name(name).schema(new StringSchema), identity)
  case class StrParamOpt(name: String) extends OptionalQueryParam[String](queryParam.name(name).schema(new StringSchema), identity)
  case class IntParam(name: String) extends MandatoryQueryParam[Int](queryParam.name(name).schema(new StringSchema), _.toInt)
  case class IntParamOpt(name: String) extends OptionalQueryParam[Int](queryParam.name(name).schema(new IntegerSchema), _.toInt)

}
