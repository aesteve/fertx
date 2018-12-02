package com.github.aesteve.fertx

abstract class MimeType(val representation: Option[String])
final class TextPlain extends MimeType(Some("text/plain"))
final class Json extends MimeType(Some("application/json"))
final class JsonUtf8 extends MimeType(Some("application/json;charset=utf8"))
final class NoContent extends MimeType(None)

object MimeType {
  val NO_CONTENT = new NoContent
  val PLAIN_TEXT = new TextPlain
  val JSON = new Json
  val JSON_UTF_8 = new JsonUtf8
}
