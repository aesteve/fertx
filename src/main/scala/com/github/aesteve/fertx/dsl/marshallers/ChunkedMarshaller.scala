package com.github.aesteve.fertx.dsl.marshallers

import com.github.aesteve.fertx.response
import com.github.aesteve.fertx.response.ResponseMarshaller
import io.vertx.core.buffer.Buffer
import io.vertx.scala.core.http.HttpServerResponse
import io.vertx.scala.core.streams.{Pump, ReadStream}

class ChunkedMarshaller[T <: ReadStream[Buffer]] extends ResponseMarshaller[response.Chunked, T] {

  override def handle(stream: T, resp: HttpServerResponse): Unit = {
    resp.setChunked(true)
    stream.endHandler(_ => resp.end())
    Pump.pump(stream, resp).start()
  }

}
