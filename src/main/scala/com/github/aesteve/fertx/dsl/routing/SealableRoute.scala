package com.github.aesteve.fertx.dsl.routing

import com.github.aesteve.fertx.response.{Response, ResponseType}
import com.github.aesteve.fertx.util.applyconverters.ApplyConverter

import scala.concurrent.Future

trait SealableRoute[T, Mime <: ResponseType] {

  def mapTuple(f: T   => Response[Mime]): FinalizedRoute

  def flatMapTuple(f: T => Future[Response[Mime]]): FinalizedRoute

}

object SealableRoute {

  abstract class CanMapToResponse[C] {
    def map(f: C): FinalizedRoute
  }

  abstract class CanFlatMapToResponse[C] {
    def flatMap(f: C): FinalizedRoute
  }

  implicit def addApplyCapability[R, Mime <: ResponseType](sealable: SealableRoute[R, Mime])(implicit hac: ApplyConverter[R, Response[Mime]]): hac.In ⇒ FinalizedRoute =
    (f: hac.In) => sealable.mapTuple(hac(f))

  implicit def addMapCapability[R, Mime <: ResponseType](sealable: SealableRoute[R, Mime])(implicit hac: ApplyConverter[R, Response[Mime]]): CanMapToResponse[hac.In] =
    (f: hac.In) => sealable(f)

  implicit def addFlatMapCapability[R, Mime <: ResponseType](sealable: SealableRoute[R, Mime])(implicit hac: ApplyConverter[R, Future[Response[Mime]]]): CanFlatMapToResponse[hac.In] =
    (f: hac.In) => sealable.flatMapTuple(hac(f))

}
