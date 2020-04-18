package io.vertx.ext.web

import io.vertx.core.http.HttpMethod

trait Router { def route(method: HttpMethod, path: String): Route }