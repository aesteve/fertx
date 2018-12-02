package com.github.aesteve.fertx.dsl

import com.github.aesteve.fertx.{Chunked, ResponseMarshaller}
import io.vertx.core.buffer.Buffer
import io.vertx.scala.core.streams.ReadStream

package object marshallers {

  implicit def chunkMarshaller[T <: ReadStream[Buffer]]: ResponseMarshaller[Chunked, T] =
    new ChunkedMarshaller[T]


}
