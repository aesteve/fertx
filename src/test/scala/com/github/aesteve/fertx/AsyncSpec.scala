package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl._
import com.github.aesteve.fertx.response.{OK, ResponseType}
import io.vertx.lang.scala.VertxExecutionContext

import scala.concurrent.Future

class AsyncSpec extends FertxTestBase with SendsDefaultText {


  "An async response" should "be received" in {
    val sent = "value"
    val ec = VertxExecutionContext(vertx.getOrCreateContext())
    GET("api" / "async")
      .produces(ResponseType.PLAIN_TEXT)
      .flatMap { () =>
        Future(OK(sent))(ec)
      }
      .attachTo(router)

    startTest { () =>
      client.get("/api/async").sendFuture().map { resp =>
        resp.statusCode should be(200)
        resp.bodyAsString.get should equal(sent)
      }
    }

  }

}
