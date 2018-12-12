package com.github.aesteve.fertx

package object media {

  trait `text/plain`
  trait `application/json`
  trait `application/json;charset=utf-8`

  trait Chunked

  implicit val `text/plain` = new  MimeType[`text/plain`] {
    override def representation: Option[String] =
      Some("text/plain")
  }

  implicit val `application/json` = new  MimeType[`application/json`] {
    override def representation: Option[String] =
      Some("application/json")
  }

  implicit val `application/json;charset=utf-8` = new MimeType[`application/json;charset=utf-8`] {
    override def representation: Option[String] =
      Some("application/json;charset=utf-8")
  }

  implicit val Chunked = new MimeType[Chunked] {
    override def representation: Option[String] =
      None
  }

  implicit val UnitMimeType = new MimeType[Unit] {
    override def representation: Option[String] =
      None
  }

}
