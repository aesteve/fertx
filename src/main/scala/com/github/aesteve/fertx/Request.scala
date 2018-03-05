package com.github.aesteve.fertx

import io.vertx.core.http.HttpMethod

trait Request {
  val path: String
  val method: HttpMethod
}
