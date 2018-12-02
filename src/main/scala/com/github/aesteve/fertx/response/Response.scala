package com.github.aesteve.fertx.response

import io.vertx.scala.core.http.HttpServerResponse

trait Response[-Mime <: ResponseType] {
  def buildResp: HttpServerResponse => Unit
}

trait ErrorMarshaller[-Mime <: ResponseType] {
  def handle(resp: HttpServerResponse, clientError: ClientError)
  def handle(resp: HttpServerResponse, error: Throwable)
}

trait ResponseMarshaller[-Mime <: ResponseType, Payload] {
  def handle(t: Payload, resp: HttpServerResponse)
}

private [fertx] class UnitResponse(val status: Int) extends Response[ResponseType] {
  override def buildResp: HttpServerResponse => Unit =
    resp =>
      resp.setStatusCode(status).end()
}

case object OK extends UnitResponse(200)
case object NotFound extends UnitResponse(404)
case object NoContentErrorMarshaller extends ErrorMarshaller[NoContent] {
  override def handle(resp: HttpServerResponse, clientError: ClientError): Unit =
    resp.setStatusCode(clientError.status).end()

  override def handle(resp: HttpServerResponse, error: Throwable): Unit =
    resp.setStatusCode(500).end()
}

case class OK[Mime <: ResponseType, Payload](payload: Payload)(implicit val marshaller: ResponseMarshaller[Mime, Payload]) extends Response[Mime] {
  override def buildResp: HttpServerResponse => Unit =
    resp =>
      marshaller.handle(payload, resp.setStatusCode(200))

}



