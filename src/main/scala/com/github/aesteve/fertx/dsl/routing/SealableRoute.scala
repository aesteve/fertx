package com.github.aesteve.fertx.dsl.routing

import com.github.aesteve.fertx.{MimeType, Response}
import com.github.aesteve.fertx.util.applyconverters.ApplyConverter

trait SealableRoute[T, Mime <: MimeType] {

  def mapTuple(f: T   => Response): FinalizedRoute
  def mapUnit(f: ()  => Response): FinalizedRoute

  private def convertApply(implicit hac: ApplyConverter[T, Response]): hac.In => FinalizedRoute =
    f =>
      mapTuple(hac(f))

}

object SealableRoute {

  abstract class CanMapToResponse[C] {
    def map(f: C): FinalizedRoute
  }

  implicit def addApplyCapability[R, Mime <: MimeType](sealable: SealableRoute[R, Mime])(implicit hac: ApplyConverter[R, Response]): hac.In ⇒ FinalizedRoute =
    sealable.convertApply(hac)


  implicit def addFoldCapability[R, Mime <: MimeType](sealable: SealableRoute[R, Mime])(implicit hac: ApplyConverter[R, Response]): CanMapToResponse[hac.In] =
    (f: hac.In) => sealable(f)
}
