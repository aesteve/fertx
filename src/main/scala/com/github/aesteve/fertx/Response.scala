package com.github.aesteve.fertx

import io.vertx.scala.core.http.HttpServerResponse

trait ResponseMarshaller[T] {
  def toStr(t: T): String
}

trait Response {
  def buildResp: HttpServerResponse => Unit
}

private [fertx] class UnitResponse(val status: Int) extends Response {
  override def buildResp: HttpServerResponse => Unit =
    resp =>
      resp.setStatusCode(status).end()
}

case object OK extends UnitResponse(200)
case object NotFound extends UnitResponse(404)

case class OK[T](payload: T)(implicit val marshaller: ResponseMarshaller[T]) extends Response {
  override def buildResp: HttpServerResponse => Unit =
    resp =>
      resp.setStatusCode(200)
        .end(marshaller.toStr(payload))
}



