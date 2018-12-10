package com.github.aesteve.fertx.request

import com.github.aesteve.fertx.response.MalformedBody
import com.timeout.docless.schema.JsonSchema
import com.timeout.docless.swagger.{BodyParameter, Operation}
import io.vertx.scala.ext.web.RoutingContext

class WithSchemaRequestUnmarshaller[Mime <: RequestType, Payload](
  unmarshaller: RequestUnmarshaller[Mime, Payload],
  schema: JsonSchema[Payload]
) extends RequestUnmarshaller[Mime, Payload] {

  override def buildOpenAPI(operation: Operation): Operation =
    operation.withParams(
      BodyParameter(
        required = true,
        schema = Some(schema.asRef)
      )
    )

  override def extract(rc: RoutingContext): Either[MalformedBody, Payload] =
    unmarshaller.extract(rc)
}
