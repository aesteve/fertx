package com.github.aesteve.fertx.http.routing

import com.github.aesteve.fertx.http._
import com.github.aesteve.fertx.utils.CanJoin

trait FRoute[Error <: HttpError, Payload] {
  def flatMap[Left <: HttpError, B](f: Payload => Either[Left, B]): FRoute[Error | Left, B]
  def seal(errorHandler: Marshaller[Error], writer: Marshaller[Payload]): Request => Response

  def map[T](mapper: Payload => T): FRoute[Error, T] = flatMap { payload => Right(mapper(payload)) }
}