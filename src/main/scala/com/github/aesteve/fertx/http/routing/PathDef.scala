package com.github.aesteve.fertx.http.routing

import com.github.aesteve.fertx.http.{HttpError, Request, RequestExtractor}
import com.github.aesteve.fertx.utils.CanJoin

enum PathDef[Error <: HttpError, Payload](val representation: String, val extractor: RequestExtractor[Error, Payload]) {
  type error = Error
  type payload = Payload
  
  case Root extends PathDef[Nothing, Unit]("/", _ =>Right(()))
  case Fragment[Error <: HttpError, Payload](override val representation: String, override val extractor: RequestExtractor[Error, Payload]) 
    extends PathDef[Error, Payload](representation, extractor)

  def /[E <: HttpError, P](other: PathDef[E, P])(using joiner: CanJoin[Payload, P]): PathDef[Error | E, joiner.Joined] =
    Fragment(s"${this.representation}/${other.representation}", request => {
      this.extractor(request) match {
        case Left(e1) => Left(e1)
        case Right(extracted) => other.extractor(request) match {
          case Left(e2) => Left(e2)
          case Right(extracted2) => Right(joiner.join(extracted, extracted2))
        }
      }
    })
}
