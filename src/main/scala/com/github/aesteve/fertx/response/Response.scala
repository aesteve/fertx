package com.github.aesteve.fertx.response

import com.github.aesteve.fertx.media.MimeType
import io.vertx.scala.core.http.HttpServerResponse

abstract class Response[-Mime](implicit mime: MimeType[Mime]) {
  def buildResp: HttpServerResponse => Unit
}



