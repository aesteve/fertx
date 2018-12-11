package com.github.aesteve.fertx.dsl.routing

import com.github.aesteve.fertx.media.MimeType
import com.github.aesteve.fertx.response.Response
import com.github.aesteve.fertx.util.applyconverters.ApplyConverter

import scala.concurrent.Future

trait SealableRoute[T, Mime] {

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

  implicit def addApplyCapability[R, Mime](sealable: SealableRoute[R, Mime])(implicit hac: ApplyConverter[R, Response[Mime]], mime: MimeType[Mime]): hac.In ⇒ FinalizedRoute =
    (f: hac.In) => sealable.mapTuple(hac(f))

  implicit def addMapCapability[R, Mime](sealable: SealableRoute[R, Mime])(implicit hac: ApplyConverter[R, Response[Mime]], mime: MimeType[Mime]): CanMapToResponse[hac.In] =
    (f: hac.In) => sealable(f)

  implicit def addFlatMapCapability[R, Mime](sealable: SealableRoute[R, Mime])(implicit hac: ApplyConverter[R, Future[Response[Mime]]], mime: MimeType[Mime]): CanFlatMapToResponse[hac.In] =
    (f: hac.In) => sealable.flatMapTuple(hac(f))

}
