package com.github.aesteve.fertx

trait ClientError
case class BadRequest(reason: String) extends ClientError
case class Unauthorized(reason: String) extends ClientError
case class Forbidden(reason: String) extends ClientError
