package com.github.aesteve.fertx

abstract class MimeType(val representation: String)
final class TextPlain extends MimeType("text/plain")
final class Json extends MimeType("application/json")
final class JsonUtf8 extends MimeType("application/json;charset=utf8")

object MimeType {
  val PLAIN_TEXT = new TextPlain
  val JSON = new Json
  val JSON_UTF_8 = new JsonUtf8
}
