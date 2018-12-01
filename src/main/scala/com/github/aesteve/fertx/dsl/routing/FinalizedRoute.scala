package com.github.aesteve.fertx.dsl.routing

import io.vertx.scala.ext.web.Router

trait FinalizedRoute {

  def attachTo(router: Router): Unit

}
