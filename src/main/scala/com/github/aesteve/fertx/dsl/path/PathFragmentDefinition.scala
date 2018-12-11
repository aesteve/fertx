package com.github.aesteve.fertx.dsl.path

import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.response.ClientError
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.parameters.Parameter
import io.vertx.scala.ext.web.RoutingContext

abstract class PathFragmentDefinition[T](parameter: Option[Parameter]) {

  def captures: Boolean
  def toRegex: String
  def fromParam: Option[String] => Either[ClientError, T]
  def at(pos: Int): PathFrag[T] = PathFrag(pos, this, parameter)

}

case class PathFrag[T](pos: Int, fragDef: PathFragmentDefinition[T], parameter: Option[Parameter]) extends Extractor[T] {

  override def getFromContext: RoutingContext => Either[ClientError, T] =
    rc =>
      fragDef.fromParam(rc.request().getParam("param" + pos))

  override def buildOpenApi(operation: Operation): Operation = {
    parameter.foreach(operation.addParametersItem)
    operation
  }

}
