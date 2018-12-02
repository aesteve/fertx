package com.github.aesteve.fertx

import com.github.aesteve.fertx.response.{ClientError, ErrorMarshaller, ResponseMarshaller}
import io.vertx.scala.core.http.HttpServerResponse

trait SendsDefaultText {

  implicit val errorTextMarshaller = new ErrorMarshaller[response.TextPlain] {

    override def handle(resp: HttpServerResponse, clientError: ClientError): Unit =
      resp.setStatusCode(clientError.status)
        .end(clientError.message.getOrElse(""))

    override def handle(resp: HttpServerResponse, error: Throwable): Unit =
      resp.setStatusCode(500)
        .end(error.getMessage)

  }


  implicit val textMarshaller = new ResponseMarshaller[response.TextPlain, String] {

    override def handle(t: String, resp: HttpServerResponse): Unit =
      resp.end(t)

  }


}
