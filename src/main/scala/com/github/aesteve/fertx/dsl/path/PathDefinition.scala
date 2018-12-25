package com.github.aesteve.fertx.dsl.path

import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.util.Tuple
import com.github.aesteve.fertx.util.TupleOps._

case class PathDefinition[T](fullPath: String, extractor: Extractor[T])(implicit ev: Tuple[T]) {

  def toFullPath: String =
    s"/$fullPath"

}

class NonFinalPathDefinition[T](override val fullPath: String, override val extractor: Extractor[T])
                               (implicit ev: Tuple[T]) extends PathDefinition[T](fullPath, extractor) {

  def /[R](other: PathFragmentDefinition[R])(implicit join: Join[T, R]): NonFinalPathDefinition[join.Out] = {
    implicit val joinProducesTuple: Tuple[join.Out] = Tuple.yes[join.Out]
    new NonFinalPathDefinition(fullPath + "/" + other.getPath, (extractor & other)(join))
  }

  def /[R](other: FinalPathFragmentDefinition[R])(implicit join: Join[T, R]): PathDefinition[join.Out] = {
    implicit val joinProducesTuple: Tuple[join.Out] = Tuple.yes[join.Out]
    PathDefinition(fullPath + "/" + other.getPath, (extractor & other)(join))
  }

}
