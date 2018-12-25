package com.github.aesteve.fertx.dsl.path

import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.response.{BadRequest, ClientError}
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.parameters.Parameter
import io.vertx.scala.ext.web.RoutingContext

import scala.util.Try

trait PathFragmentDefinition[T] extends Extractor[T] {
  def getPath: String
  def getFromContext: RoutingContext => Either[ClientError, T]
  def as[R](implicit mapper: T => R): PathFragmentDefinition[Tuple1[R]] =
    new PathFragmentDefinition[Tuple1[R]] {
      override def getPath: String = PathFragmentDefinition.this.getPath
      override def getFromContext: RoutingContext => Either[ClientError, Tuple1[R]] = rc => {
        PathFragmentDefinition.this.getFromContext(rc) match {
          case Right(value) => Try(mapper(value))
            .fold(e => Left(BadRequest(s"Could not convert $value. ${e.getMessage}")), i => Right(Tuple1(i)))
          case error => error.asInstanceOf
        }
      }
      override def buildOpenApi(operation: Operation): Operation =
        PathFragmentDefinition.this.buildOpenApi(operation)
    }
}

trait FinalPathFragmentDefinition[T] extends PathFragmentDefinition[T]

abstract class UnitPathFragment(path: String) extends PathFragmentDefinition[Unit] {
  override def getPath: String = path
  override def getFromContext: RoutingContext => Either[ClientError, Unit] = _ =>
    Right((): Unit)

  override def buildOpenApi(operation: Operation): Operation =
    operation
}

abstract class CapturesPathFragment[T](parameter: Parameter) extends PathFragmentDefinition[T] {

  def fromParam: Option[String] => Either[ClientError, T]

  override def getPath: String =
    s":${parameter.getName}"

  override def getFromContext: RoutingContext => Either[ClientError, T] =
    rc =>
      fromParam(rc.request().getParam(parameter.getName))

  override def buildOpenApi(operation: Operation): Operation =
    operation.addParametersItem(parameter)

}
