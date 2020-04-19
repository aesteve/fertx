package com.github.aesteve.fertx.utils

import scala.util.Try
import com.github.aesteve.fertx.http.{ HttpError, FError }

extension TryOrFailWithHttpError {

  def [T](t: Try[T]).orHttpError(errorHandler: Throwable => FError): Either[HttpError, T] =
  t.toEither
    .left
    .map(errorHandler)

  def [T](t: Try[T]).orHttpError(status: Int, msg: Option[String]): Either[HttpError, T] =
    t.orHttpError(FError.WithCauseAndMessage(status, _, msg))

  def [T](t: Try[T]).orHttpError(error: FError): Either[HttpError, T] =
    t.orHttpError { _ => error }
  
}
