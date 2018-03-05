package com.github.aesteve.fertx.dsl.path

import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.util.Tuple
import com.github.aesteve.fertx.util.TupleOps._

case class PathDef[T](regex: String, extractor: Extractor[T], maxPos: Int = 0)(implicit ev: Tuple[T]) {

  /*
  def toPath: String =
    contracts.map(_.toRegex).reverse.mkString("/", "/", "")
  */

  def toFullPath: String =
    s"\\/$regex"

  def /[R](other: PathFragDef[R])(implicit join: Join[T, R]): PathDef[join.Out] = {
    implicit val joinProducesTuple = Tuple.yes[join.Out]
    PathDef(regex + "\\/" + other.toRegex, (extractor & other.at(maxPos + 1))(join), maxPos + 1)
  }

  // private def tHandler(h: T => Response): Unit = ???

}
