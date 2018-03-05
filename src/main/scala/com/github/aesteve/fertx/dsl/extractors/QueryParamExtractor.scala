package com.github.aesteve.fertx.dsl.extractors

import com.github.aesteve.fertx.ClientError
import io.vertx.scala.ext.web.RoutingContext

abstract class QueryParamExtractor[T](name: String) extends Extractor[T] {

  def fromReq: Option[String] => Either[ClientError, T]

  override def getFromContext: RoutingContext => Either[ClientError, T] =
    rc => fromReq(rc.request.getParam(name))

}
