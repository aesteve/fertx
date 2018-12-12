package com.github.aesteve.fertx.request

import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.media.MimeType
import com.github.aesteve.fertx.response.{ClientError, MalformedBody}
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.{Content, MediaType, Schema}
import io.swagger.v3.oas.models.parameters.RequestBody
import io.vertx.scala.ext.web.RoutingContext

abstract class RequestUnmarshaller[Mime, Payload](implicit mime: MimeType[Mime]) extends Extractor[Tuple1[Payload]] {

  def schema: Schema[Payload]

  override def needsBody: Boolean = true

  def extract(rc: RoutingContext): Either[MalformedBody, Payload]

  override def getFromContext: RoutingContext => Either[ClientError, Tuple1[Payload]] =
    rc =>
      extract(rc).map(Tuple1(_))

  override def buildOpenApi(operation: Operation): Operation = {
    mime.representation.foreach { repr =>
      operation.requestBody(
        new RequestBody().content(
          new Content()
            .addMediaType(repr, new MediaType().schema(schema))
        )
      )
    }
    operation
  }

}
