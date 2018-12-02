package com.github.aesteve.fertx.dsl.marshallers

import com.github.aesteve.fertx.response
import com.github.aesteve.fertx.response.{ClientError, ErrorMarshaller, ResponseMarshaller}
import io.vertx.core.buffer.Buffer
import io.vertx.scala.core.http.HttpServerResponse
import io.vertx.scala.core.streams.{Pump, ReadStream}

class ChunkedMarshaller[T <: ReadStream[Buffer]]
  extends ResponseMarshaller[response.Chunked, T]
  with ErrorMarshaller[response.Chunked] {

  override def handle(stream: T, resp: HttpServerResponse): Unit = {
    resp.setChunked(true)
    stream.endHandler(_ => resp.end())
    Pump.pump(stream, resp).start()
  }

  override def handle(resp: HttpServerResponse, clientError: ClientError): Unit =
    resp.setStatusCode(clientError.status)
      .end(clientError.message.getOrElse(""))

  override def handle(resp: HttpServerResponse, error: Throwable): Unit =
    resp.setStatusCode(500)
      .end(error.getMessage)

}
