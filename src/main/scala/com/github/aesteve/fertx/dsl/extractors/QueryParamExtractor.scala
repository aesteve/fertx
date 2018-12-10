package com.github.aesteve.fertx.dsl.extractors

import com.github.aesteve.fertx.response.ClientError
import com.timeout.docless.swagger.{Operation, Parameter}
import io.vertx.scala.ext.web.RoutingContext

abstract class QueryParamExtractor[T](name: String, specificParams: Parameter => Parameter) extends Extractor[T] {

  def fromReq: Option[String] => Either[ClientError, T]

  override def getFromContext: RoutingContext => Either[ClientError, T] =
    rc =>
      fromReq(rc.request.getParam(name))

  override def buildOpenAPI(operation: Operation): Operation = {
    val param = Parameter.query(
      name
    )
    specificParams(param)
    operation.withParams(param)
  }

}
