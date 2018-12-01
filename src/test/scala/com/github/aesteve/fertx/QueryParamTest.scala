package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl._

class QueryParamTest extends FertxTestBase {

  "Mandatory query param" should "not be missing" in {
    GET("api" / "optionalparam")
      .query("optionalparameter")
      .map { param =>
        OK(param)
      }.attachTo(router)
    server.requestHandler(router.accept).listenFuture().flatMap { _ =>
      client.get("/api/optionalparam").sendFuture().map { response =>
        response.statusCode should be (400)
      }
    }
  }

}
