package com.github.aesteve.fertx.response

import io.vertx.scala.core.http.HttpServerResponse

trait ErrorMarshaller[-Mime] {
  def handle(resp: HttpServerResponse, clientError: ClientError)
  def handle(resp: HttpServerResponse, error: Throwable)
}

