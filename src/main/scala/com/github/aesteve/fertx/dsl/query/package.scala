package com.github.aesteve.fertx.dsl

import com.github.aesteve.fertx.response.{BadRequest, ClientError}
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter

import scala.util.Try

package object query {

  def queryParam: Parameter =
    new Parameter().in("query")

  class OptionalQueryParamDefinition[T](parameter: Parameter, mapper: String => T)
    extends QueryParamExtractor[Tuple1[Option[T]]](parameter.required(false)) {

    def as[R](implicit schema: Schema[R], newMapper: T => R): OptionalQueryParamDefinition[R] =
      new OptionalQueryParamDefinition[R](parameter.schema(schema), mapper andThen newMapper)

    override def fromReq: Option[String] => Either[ClientError, Tuple1[Option[T]]] = {
      case None => Right(Tuple1(None))
      case Some(value) => Try(mapper(value))
        .fold(_ => Left(BadRequest(s"Cannot read parameter ${parameter.getName}")), mapped => Right(Tuple1(Some(mapped))))
    }

  }

  class MandatoryQueryParamDefinition[T](parameter: Parameter, mapper: String => T)
    extends QueryParamExtractor[Tuple1[T]](parameter.required(true)) {

    def ? : OptionalQueryParamDefinition[T] =
      new OptionalQueryParamDefinition[T](parameter, mapper)

    def as[R](implicit schema: Schema[R], newMapper: T => R): MandatoryQueryParamDefinition[R] =
      new MandatoryQueryParamDefinition[R](parameter.schema(schema), mapper andThen newMapper)

    override def fromReq: Option[String] => Either[ClientError, Tuple1[T]] = {
      case None => Left(BadRequest(s"Query parameter ${parameter.getName} is missing"))
      case Some(value) => Try(mapper(value))
        .fold(_ => Left(BadRequest(s"Cannot read parameter ${parameter.getName}")), mapped => Right(Tuple1(mapped)))
    }

  }

}
