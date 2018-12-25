package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl._
import com.github.aesteve.fertx.media._
import com.github.aesteve.fertx.response.OK
import io.vertx.lang.scala.VertxExecutionContext

import scala.concurrent.Future

class AsyncSpec extends FertxTestBase with SendsDefaultText {


  "An async response" should "be received" in {
    val sent = "value"
    implicit val ec = VertxExecutionContext(vertx.getOrCreateContext())
    route =
      GET("api" / "async")
        .produces[`text/plain`]
        .flatMap { () =>
          Future(
            OK(sent)
          )
        }

    startTest { () =>
      get("/api/async").sendFuture().map { resp =>
        resp.statusCode should be(200)
        resp.bodyAsString.get should equal(sent)
      }
    }

  }

}
