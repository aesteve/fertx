package com.github.aesteve.fertx

import java.nio.file.Paths

import com.github.aesteve.fertx.dsl._
import com.github.aesteve.fertx.dsl.marshallers.ChunkedMarshaller
import com.github.aesteve.fertx.response._
import io.vertx.core.Handler
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpHeaders
import io.vertx.lang.scala.json.JsonObject
import io.vertx.scala.core.file.{AsyncFile, OpenOptions}
import io.vertx.scala.core.http.HttpServerResponse
import io.vertx.scala.core.streams.ReadStream

class ResponseTypeSpec extends FertxTestBase {

  case class Player(firstname: String, lastname: String)
  private val Goat = Player("Michael", "Jordan")

  "A text response " should "be created according to implicit marshallers" in {
    implicit val PlayerTextMarshaller: ResponseMarshaller[TextPlain, Player] =
      (p: Player, resp: HttpServerResponse) =>
        resp.end(s"${p.firstname}:${p.lastname}")
    GET("some" / "text")
      .produces(ResponseType.PLAIN_TEXT).map { () =>
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
      .produces(ResponseType.JSON)
      .map { () => OK(Goat) }
      .attachTo(router)
    startTest { () =>
      client.get("/some/text").sendFuture().map { resp =>
        resp.statusCode should be(200)
        resp.getHeader(HttpHeaders.CONTENT_TYPE.toString) should equal(ResponseType.JSON.representation)
        resp.bodyAsJsonObject should equal(Some(createJson(Goat)))
      }
    }
  }

  "A chunked response" should "be possible" in {
    val path = Paths.get(ClassLoader.getSystemResource("1000lines.txt").toURI).toAbsolutePath
    vertx.fileSystem().openFuture(path.toString, OpenOptions().setRead(true)).flatMap { file =>
      file.setReadBufferSize(100)
      import com.github.aesteve.fertx.dsl.marshallers.chunkMarshaller
      GET("chunked")
        .produces(ResponseType.CHUNKED)
        .map { () => OK(file) }
        .attachTo(router)
      startTest { () =>
        client.get("/chunked").sendFuture().map { resp =>
          resp.statusCode should be(200)
          resp.body should not be empty
          resp.body.get.length should be > 100
        }
      }
    }

  }

}
