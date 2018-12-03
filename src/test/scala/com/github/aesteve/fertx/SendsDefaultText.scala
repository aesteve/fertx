package com.github.aesteve.fertx

import com.github.aesteve.fertx.response.ResponseMarshaller
import com.github.aesteve.fertx.dsl.marshallers.SimpleErrorTextMarshaller
import io.vertx.scala.core.http.HttpServerResponse

trait SendsDefaultText {

  implicit val errorTextMarshaller = SimpleErrorTextMarshaller

  implicit val textMarshaller = new ResponseMarshaller[response.TextPlain, String] {

    override def handle(t: String, resp: HttpServerResponse): Unit =
      resp.end(t)

  }

}
