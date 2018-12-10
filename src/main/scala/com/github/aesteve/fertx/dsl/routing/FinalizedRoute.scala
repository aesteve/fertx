package com.github.aesteve.fertx.dsl.routing

import com.timeout.docless.swagger.Path
import io.vertx.scala.ext.web.Router

trait FinalizedRoute {

  def attachTo(router: Router): Unit
  def openAPIOperation: Path

}
