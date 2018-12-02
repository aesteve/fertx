package com.github.aesteve.fertx

import io.vertx.scala.core.http.HttpServerResponse

trait Response[-Mime <: MimeType] {
  def buildResp: HttpServerResponse => Unit
}

trait ResponseMarshaller[-Mime <: MimeType, Payload] {
  def handle(t: Payload, resp: HttpServerResponse)
}

private [fertx] class UnitResponse(val status: Int) extends Response[MimeType] {
  override def buildResp: HttpServerResponse => Unit =
    resp =>
      resp.setStatusCode(status).end()
}

case object OK extends UnitResponse(200)
case object NotFound extends UnitResponse(404)

case class OK[Mime <: MimeType, Payload](payload: Payload)(implicit val marshaller: ResponseMarshaller[Mime, Payload]) extends Response[Mime] {
  override def buildResp: HttpServerResponse => Unit =
    resp =>
      marshaller.handle(payload, resp.setStatusCode(200))

}



