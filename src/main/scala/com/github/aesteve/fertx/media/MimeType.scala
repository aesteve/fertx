package com.github.aesteve.fertx.media

trait MimeType[Mime] {

  def representation: Option[String]

}



