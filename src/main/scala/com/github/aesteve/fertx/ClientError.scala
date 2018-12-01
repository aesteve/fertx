package com.github.aesteve.fertx

abstract class ClientError(val status: Int, val message: Option[String])
case class BadRequest(reason: String) extends ClientError(400, Some(reason))
case class Unauthorized(reason: String) extends ClientError(401, Some(reason))
case class Forbidden(reason: String) extends ClientError(403, Some(reason))
