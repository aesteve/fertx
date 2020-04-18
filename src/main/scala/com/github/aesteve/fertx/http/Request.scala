package com.github.aesteve.fertx.http

trait Request {
  def getParam(name: String): String | Null
}

type RequestExtractor[Error <: HttpError, Payload] = Request => Either[Error, Payload]
