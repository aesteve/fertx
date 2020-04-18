package com.github.aesteve.fertx

import com.github.aesteve.fertx.http.routing.{FRoute, PathDef}
import com.github.aesteve.fertx.http.{HttpError, Request, Response}
import com.github.aesteve.fertx.utils.CanJoin

object DSL {
  export com.github.aesteve.fertx.{ given _ }
  export com.github.aesteve.fertx.utils.TupleJoins.{ given _ }
  export com.github.aesteve.fertx.http.routing.Params._
  
  def GET[E <: HttpError, P](path: PathDef[E, P]): FRoute[path.error, path.payload] = ???
  
}
