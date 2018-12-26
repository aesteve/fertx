package com.github.aesteve.fertx.dsl.extractors

import com.github.aesteve.fertx.response.ClientError
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.parameters.Parameter
import io.vertx.scala.ext.web.RoutingContext

abstract class QueryParamExtractor[T](parameter: Parameter) extends Extractor[T] {

  def fromReq: Option[String] => Either[ClientError, T]

  override def getFromContext: RoutingContext => Either[ClientError, T] =
    rc =>
      fromReq(rc.request.getParam(parameter.getName))

  override def buildOpenApi(operation: Operation): Operation =
    operation.addParametersItem(parameter)

}
