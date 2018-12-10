package com.github.aesteve.fertx.request

import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.response.{ClientError, MalformedBody}
import com.timeout.docless.swagger.Operation
import io.vertx.scala.ext.web.RoutingContext

trait RequestUnmarshaller[Mime <: RequestType, Payload] extends Extractor[Tuple1[Payload]] {

  override def needsBody: Boolean = true

  def extract(rc: RoutingContext): Either[MalformedBody, Payload]

  override def getFromContext: RoutingContext => Either[ClientError, Tuple1[Payload]] =
    rc =>
      extract(rc).map(Tuple1(_))

  override def buildOpenAPI(operation: Operation): Operation =
    operation

}
