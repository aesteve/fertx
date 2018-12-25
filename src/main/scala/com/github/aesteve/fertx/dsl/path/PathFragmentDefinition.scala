package com.github.aesteve.fertx.dsl.path

import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.response.ClientError
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.parameters.Parameter
import io.vertx.scala.ext.web.RoutingContext

trait PathFragmentDefinition[T] extends Extractor[T] {
  def getPath: String
  def getFromContext: RoutingContext => Either[ClientError, T]
}

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
