package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl.path.{PathFragmentDefinition, _}
import com.github.aesteve.fertx.dsl.query._
import com.github.aesteve.fertx.dsl.routing.RouteDefinition
import com.github.aesteve.fertx.dsl.routing.impl.RouteDefinitionImpl
import com.github.aesteve.fertx.media._
import com.github.aesteve.fertx.response.{ClientError, UnitErrorMarshaller}
import io.swagger.v3.oas.models.media.{Schema, StringSchema}
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

  implicit val shortTupleMapper:    Tuple1[String] => Short = _._1.toShort
  implicit val intTupleMapper:      Tuple1[String] => Int = _._1.toInt
  implicit val longTupleMapper:     Tuple1[String] => Long = _._1.toLong
  implicit val doubleTupleMapper:   Tuple1[String] => Double = _._1.toDouble
  implicit val floatTupleMapper:    Tuple1[String] => Float = _._1.toFloat
  implicit val booleanTupleMapper:  Tuple1[String] => Boolean = _._1.toBoolean

  implicit def shortSchema: Schema[Short] =
    new Schema[Short].`type`("integer").asInstanceOf[Schema[Short]]

  implicit def intSchema: Schema[Int] =
    new Schema[Integer].`type`("integer").format("int32").asInstanceOf[Schema[Int]]

  implicit def longSchema: Schema[Long] =
    new Schema[Long].`type`("integer").format("int64").asInstanceOf[Schema[Long]]

  implicit def doubleSchema: Schema[Double] =
    new Schema[Double].`type`("number").format("double").asInstanceOf[Schema[Double]]

  implicit def floatSchema: Schema[Float] =
    new Schema[Float].`type`("number").format("float").asInstanceOf[Schema[Float]]

  implicit def pathFragDefFromStr(str: String): FixedPath =
    FixedPath(str)

  implicit def pathFragDefFromSymbol(symbol: Symbol): PathFragmentDefinition[Tuple1[String]] =
    StrPath(symbol.name)

  implicit def pathDefFromSymbol(symbol: Symbol): NonFinalPathDefinition[Tuple1[String]] =
    new NonFinalPathDefinition(symbol.name, StrPath(symbol.name))

  implicit def pathDefFromStr(str: String): NonFinalPathDefinition[Unit] =
    new NonFinalPathDefinition(str, FixedPath(str))

  /* Query parameters */
  implicit def mandatoryParamFromStr(str: String): QueryParam[String] =
    new QueryParam[String](queryParam.name(str).schema(new StringSchema), identity)

}
