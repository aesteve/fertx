package com.github.aesteve.fertx.dsl.path

import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.util.Tuple
import com.github.aesteve.fertx.util.TupleOps._

case class PathDefinition[T](regex: String, extractor: Extractor[T], maxPos: Int = -1)(implicit ev: Tuple[T]) {

  def toFullPath: String =
    s"\\/$regex"

  def /[R](other: PathFragmentDefinition[R])(implicit join: Join[T, R]): PathDefinition[join.Out] = {
    implicit val joinProducesTuple = Tuple.yes[join.Out]
    val nextPos = if (other.captures) maxPos + 1 else maxPos
    PathDefinition(regex + "\\/" + other.toRegex, (extractor & other.at(nextPos))(join), nextPos)
  }

}
