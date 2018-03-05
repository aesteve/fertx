package com.github.aesteve.fertx.dsl.extractors

import com.github.aesteve.fertx.ClientError
import com.github.aesteve.fertx.util.Tuple
import com.github.aesteve.fertx.util.TupleOps.Join
import io.vertx.scala.ext.web.RoutingContext

abstract class Extractor[T] {

  def getFromContext: RoutingContext => Either[ClientError, T]

  def &[R](other: Extractor[R])(implicit join: Join[T,R]): Extractor[join.Out] = {
    implicit val joinProducesTuple = Tuple.yes[join.Out]
    new Extractor[join.Out]() {
      override def getFromContext: RoutingContext => Either[ClientError, join.Out] =
        rc => {
          val thisRes: Either[ClientError, T] = Extractor.this.getFromContext(rc)
          val otherRes: Either[ClientError, R] = other.getFromContext(rc)
          thisRes.flatMap { t =>
            otherRes.map { r =>
              join(t, r)
            }
          }
        }
    }
  }



}
