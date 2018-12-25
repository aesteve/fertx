package com.github.aesteve.fertx.dsl.path

import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.util.Tuple
import com.github.aesteve.fertx.util.TupleOps._

case class PathDefinition[T](fullPath: String, extractor: Extractor[T])(implicit ev: Tuple[T]) {

  def toFullPath: String =
    s"/$fullPath"

  def /[R](other: PathFragmentDefinition[R])(implicit join: Join[T, R]): PathDefinition[join.Out] = {
    implicit val joinProducesTuple = Tuple.yes[join.Out]
    PathDefinition(fullPath + "/" + other.getPath, (extractor & other)(join))
  }

}
