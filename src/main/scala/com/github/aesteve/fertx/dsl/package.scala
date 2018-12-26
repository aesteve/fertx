package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl.path.{PathFragmentDefinition, _}
import com.github.aesteve.fertx.dsl.query._
import com.github.aesteve.fertx.dsl.routing.RouteDefinition
import com.github.aesteve.fertx.dsl.routing.impl.RouteDefinitionImpl
import com.github.aesteve.fertx.media._
import com.github.aesteve.fertx.response.{ClientError, UnitErrorMarshaller}
import io.swagger.v3.oas.models.media.{IntegerSchema, StringSchema}
import io.swagger.v3.oas.models.parameters.PathParameter
import io.vertx.core.http.HttpMethod

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
  case class StrParam(name: String) extends MandatoryQueryParamDefinition[String](queryParam.name(name).schema(new StringSchema), identity)
  case class StrParamOpt(name: String) extends OptionalQueryParamDefinition[String](queryParam.name(name).schema(new StringSchema), identity)
  case class IntParam(name: String) extends MandatoryQueryParamDefinition[Int](queryParam.name(name).schema(new StringSchema), _.toInt)
  case class IntParamOpt(name: String) extends OptionalQueryParamDefinition[Int](queryParam.name(name).schema(new IntegerSchema), _.toInt)

}
