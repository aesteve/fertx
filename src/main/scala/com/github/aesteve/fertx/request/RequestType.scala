package com.github.aesteve.fertx.request

abstract class RequestType(val representation: Option[String])

final class TextPlain extends RequestType(Some("text/plain"))

final class Json extends RequestType(Some("application/json"))

final class JsonUtf8 extends RequestType(Some("application/json;charset=utf8"))

final class NoContent extends RequestType(None)

final class Chunked extends RequestType(None)

object RequestType {
  val NO_CONTENT = new NoContent
  val PLAIN_TEXT = new TextPlain
  val JSON = new Json
  val JSON_UTF_8 = new JsonUtf8
  val CHUNKED = new Chunked
}
