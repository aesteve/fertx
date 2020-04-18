package com.github.aesteve.fertx.http.routing.impl

import com.github.aesteve.fertx.http._
import com.github.aesteve.fertx.http.routing.PathDef
import io.vertx.core.http.HttpMethod

class FRouteFromPathAndMethod[Error <: HttpError, Payload](path: PathDef[Error, Payload], method: HttpMethod)
  extends FRouteWithPayload[Error, Payload]({ _.route(method, path.representation) }, path.extractor)