package com.github.aesteve.fertx

import com.github.aesteve.fertx.media.MimeType
import io.vertx.scala.core.http.HttpServerResponse

package object response {

  private implicit val AnyMime = new MimeType[Any] {
    override def representation: Option[String] = None
  }
  private [fertx] class AnyResponse(val status: Int) extends Response[Any] {
    override def buildResp: HttpServerResponse => Unit =
      resp =>
        resp.setStatusCode(status).end()
  }

  case object OK extends AnyResponse(200)
  case object NotFound extends AnyResponse(404)
  case object UnitErrorMarshaller extends ErrorMarshaller[Unit] {
    override def handle(resp: HttpServerResponse, clientError: ClientError): Unit =
      resp.setStatusCode(clientError.status).end()

    override def handle(resp: HttpServerResponse, error: Throwable): Unit =
      resp.setStatusCode(500).end()
  }

  case class OK[Mime, Payload](payload: Payload)(implicit val marshaller: ResponseMarshaller[Mime, Payload], mime: MimeType[Mime]) extends Response[Mime] {
    override def buildResp: HttpServerResponse => Unit =
      resp =>
        marshaller.handle(payload, resp.setStatusCode(200))

  }

}
