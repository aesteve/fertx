package com.github.aesteve.fertx.dsl

import com.github.aesteve.fertx.response.{BadRequest, ClientError}
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.parameters.Parameter

import scala.util.Try

package object query {

  def queryParam: Parameter =
    new Parameter().in("query")

  class OptionQueryParam[T](parameter: Parameter, mapper: String => T)
    extends QueryParamExtractor[Tuple1[Option[T]]](parameter.required(false)) {

    def as[R](implicit schema: Schema[R], newMapper: Tuple1[T] => R): OptionQueryParam[R] =
      new OptionQueryParam[R](parameter.schema(schema), str => newMapper(Tuple1(mapper(str))))

    override def fromReq: Option[String] => Either[ClientError, Tuple1[Option[T]]] = {
      case None => Right(Tuple1(None))
      case Some(value) => Try(mapper(value))
        .fold(_ => Left(BadRequest(s"Cannot read parameter ${parameter.getName}")), mapped => Right(Tuple1(Some(mapped))))
    }

  }

  class QueryParam[T](parameter: Parameter, mapper: String => T)
    extends QueryParamExtractor[Tuple1[T]](parameter.required(true)) {

    def ? : OptionQueryParam[T] =
      new OptionQueryParam[T](parameter, mapper)

    def as[R](implicit schema: Schema[R], newMapper: Tuple1[T] => R): QueryParam[R] =
      new QueryParam[R](parameter.schema(schema), str => newMapper(Tuple1(mapper(str))))

    override def fromReq: Option[String] => Either[ClientError, Tuple1[T]] = {
      case None => Left(BadRequest(s"Query parameter ${parameter.getName} is missing"))
      case Some(value) => Try(mapper(value))
        .fold(_ => Left(BadRequest(s"Cannot read parameter ${parameter.getName}")), mapped => Right(Tuple1(mapped)))
    }

  }

}
