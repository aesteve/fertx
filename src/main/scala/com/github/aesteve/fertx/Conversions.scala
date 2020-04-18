package com.github.aesteve.fertx

import com.github.aesteve.fertx.http.{Request, Response}
import com.github.aesteve.fertx.http.routing.{FRoute, PathDef}

given strConversion as Conversion[String, PathDef.Fragment[Nothing, Unit]] = new Conversion[String, PathDef.Fragment[Nothing, Unit]] {
  override def apply(str: String) = new PathDef.Fragment[Nothing, Unit](str, _ => Right(()))
}

given as Conversion[FRoute[Nothing, Unit], Request => Response] = new Conversion[FRoute[Nothing, Unit], Request => Response] {
  override def apply(route: FRoute[Nothing, Unit]): Request => Response = { _ => Response.NoContent }
}
