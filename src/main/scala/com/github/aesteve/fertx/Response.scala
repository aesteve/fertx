package com.github.aesteve.fertx

trait Response
trait ResponseMarshaller[T] {
  def toStr(t: T): String
}
case object OK extends Response
case class OK[T](payload: T)(implicit val marshaller: ResponseMarshaller[T]) extends Response

case object NotFound extends Response

