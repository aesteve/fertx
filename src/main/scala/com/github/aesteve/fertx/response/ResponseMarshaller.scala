package com.github.aesteve.fertx.response

import io.vertx.scala.core.http.HttpServerResponse

abstract class ResponseMarshaller[-Mime, Payload] {
  def handle(t: Payload, resp: HttpServerResponse)
}

