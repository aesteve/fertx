package com.github.aesteve.fertx.dsl.path

import com.github.aesteve.fertx.ClientError
import com.github.aesteve.fertx.dsl.extractors.Extractor
import io.vertx.scala.ext.web.RoutingContext

abstract class PathFragmentDefinition[T] {

  def captures: Boolean
  def toRegex: String
  def fromParam: Option[String] => Either[ClientError, T]
  def at(pos: Int): PathFrag[T] = PathFrag(pos, this)

}

case class PathFrag[T](pos: Int, fragDef: PathFragmentDefinition[T]) extends Extractor[T] {

  override def getFromContext: RoutingContext => Either[ClientError, T] =
    rc =>
      fragDef.fromParam(rc.request().getParam("param" + pos))

}