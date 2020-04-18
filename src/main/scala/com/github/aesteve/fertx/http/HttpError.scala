package com.github.aesteve.fertx.http

type HttpError = FError | Nothing
enum FError(val status: Int, val message: Option[String]) {
  case Raw(override val status: Int) extends FError(status, None)
  case WithMessage(override val status: Int, msg: String) extends FError(status, Some(msg))
  case WithCause(override val status: Int, t: Throwable) extends FError(status, Some(t.getMessage))
  case WithCauseAndMessage(override val status: Int, t: Throwable, msg: Option[String]) 
    extends FError(status, msg match {
      case Some(overridenMsg) => Some(s"$msg. Cause: ${t.getMessage}")
      case None => Some(t.getMessage)
    })
}