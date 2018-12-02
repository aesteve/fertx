package com.github.aesteve.fertx.response

import io.vertx.scala.core.http.HttpServerResponse

trait Response[-Mime <: ResponseType] {
  def buildResp: HttpServerResponse => Unit
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

case class OK[Mime <: ResponseType, Payload](payload: Payload)(implicit val marshaller: ResponseMarshaller[Mime, Payload]) extends Response[Mime] {
  override def buildResp: HttpServerResponse => Unit =
    resp =>
      marshaller.handle(payload, resp.setStatusCode(200))

}



