package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl._
import io.vertx.core.http.HttpHeaders
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.http.HttpServerResponse

class MimeTypeSpec extends FertxTestBase {

  case class Player(firstname: String, lastname: String)
  private val Goat = Player("Michael", "Jordan")

  "A text response " should "be created according to implicit marshallers" in {
    implicit val PlayerTextMarshaller: ResponseMarshaller[TextPlain, Player] =
      (p: Player, resp: HttpServerResponse) =>
        resp.end(s"${p.firstname}:${p.lastname}")
    GET("some" / "text") { () =>
      OK(Goat)
    }.attachTo(router)
    startTest { () =>
      client.get("/some/text").sendFuture().map { resp =>
        resp.statusCode should be(200)
        resp.bodyAsString should be(Some(s"${Goat.firstname}:${Goat.lastname}"))
      }
    }
  }

  "A Json response" should "work the same way" in {
    val createJson = (p: Player) => new JsonObject().put("firstname", p.firstname).put("lastname", p.lastname)
    implicit val PlayerTextMarshaller: ResponseMarshaller[Json, Player] =
      (p: Player, resp: HttpServerResponse) =>
        resp.end(createJson(p).encode())
    GET("some" / "text")
      .produces(MimeType.JSON)
      .map { () => OK(Goat) }
      .attachTo(router)
    startTest { () =>
      client.get("/some/text").sendFuture().map { resp =>
        resp.statusCode should be(200)
        resp.getHeader(HttpHeaders.CONTENT_TYPE.toString) should be(Some(MimeType.JSON.representation))
        resp.bodyAsJsonObject should equal(Some(createJson(Goat)))
      }
    }
  }

}
