package com.github.aesteve.fertx.dsl

import io.vertx.core.buffer.Buffer
import io.vertx.scala.core.streams.ReadStream

package object marshallers {

  implicit def chunkMarshaller[T <: ReadStream[Buffer]] =
    new ChunkedMarshaller[T]


}
