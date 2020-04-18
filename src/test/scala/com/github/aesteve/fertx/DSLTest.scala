package com.github.aesteve.fertx

import com.github.aesteve.fertx.DSL.{_, given _}
import com.github.aesteve.fertx.http.{FError, HttpError, Request, Response}
import com.github.aesteve.fertx.http.routing.PathDef
import com.github.aesteve.fertx.http.routing.Params.Path


class DSLTest {
  import com.github.aesteve.fertx.utils.TupleJoins.{_, given _} // hopefully it's a bug from 0.24
  
  val pureGet: Request => Response = GET("something")
  val apiGet: Request => Response = GET[Nothing, Unit]("api" / "v1")
  val apiGetParam = GET[HttpError, (Int, Int)]( "api" / "v1" / Path.int("param1") / "something" / Path.int("param2"))
    // .queryPa
    .map { _ + _ }
}
