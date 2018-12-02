package com.github.aesteve.fertx.dsl.routing

import com.github.aesteve.fertx.response.{Response, ResponseType}
import com.github.aesteve.fertx.util.applyconverters.ApplyConverter

trait SealableRoute[T, Mime <: ResponseType] {

  def mapTuple(f: T   => Response[Mime]): FinalizedRoute
  def mapUnit(f: ()  => Response[Mime]): FinalizedRoute

  private def convertApply(implicit hac: ApplyConverter[T, Response[Mime]]): hac.In => FinalizedRoute =
    f =>
      mapTuple(hac(f))

}

object SealableRoute {

  abstract class CanMapToResponse[C] {
    def map(f: C): FinalizedRoute
  }

  implicit def addApplyCapability[R, Mime <: ResponseType](sealable: SealableRoute[R, Mime])(implicit hac: ApplyConverter[R, Response[Mime]]): hac.In ⇒ FinalizedRoute =
    sealable.convertApply(hac)


  implicit def addFoldCapability[R, Mime <: ResponseType](sealable: SealableRoute[R, Mime])(implicit hac: ApplyConverter[R, Response[Mime]]): CanMapToResponse[hac.In] =
    (f: hac.In) => sealable(f)
}
