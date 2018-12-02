package com.github.aesteve.fertx.response

abstract class ResponseType(val representation: Option[String])
final class TextPlain extends ResponseType(Some("text/plain"))
final class Json extends ResponseType(Some("application/json"))
final class JsonUtf8 extends ResponseType(Some("application/json;charset=utf8"))
final class NoContent extends ResponseType(None)
final class Chunked extends ResponseType(None)

object ResponseType {
  val NO_CONTENT = new NoContent
  val PLAIN_TEXT = new TextPlain
  val JSON = new Json
  val JSON_UTF_8 = new JsonUtf8
  val CHUNKED = new Chunked
}
