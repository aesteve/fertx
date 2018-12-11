package com.github.aesteve.fertx.dsl

import com.github.aesteve.fertx.media._
import com.github.aesteve.fertx.response.{ClientError, ErrorMarshaller}
import io.vertx.core.buffer.Buffer
import io.vertx.scala.core.http.HttpServerResponse
import io.vertx.scala.core.streams.ReadStream

package object marshallers {

  implicit def chunkMarshaller[T <: ReadStream[Buffer]] =
    new ChunkedMarshaller[T]

  implicit val SimpleErrorTextMarshaller = new ErrorMarshaller[`text/plain`] {

    override def handle(resp: HttpServerResponse, clientError: ClientError): Unit =
      resp.setStatusCode(clientError.status)
        .end(clientError.message.getOrElse(""))

    override def handle(resp: HttpServerResponse, error: Throwable): Unit =
      resp.setStatusCode(500)
        .end(error.getMessage)

  }
}
