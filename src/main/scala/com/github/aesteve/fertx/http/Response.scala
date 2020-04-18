package com.github.aesteve.fertx.http

enum Response(status: Int) {
  case NoContent extends Response(204)
}
type Marshaller[T] = T => Response