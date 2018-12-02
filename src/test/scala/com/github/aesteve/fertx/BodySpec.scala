package com.github.aesteve.fertx

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.github.aesteve.fertx.dsl._
import com.github.aesteve.fertx.request.{RequestType, RequestUnmarshaller, TextPlain}
import com.github.aesteve.fertx.response.{MalformedBody, OK, ResponseMarshaller, ResponseType}
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.Json
import io.vertx.scala.core.http.HttpServerResponse
import io.vertx.scala.ext.web.RoutingContext


case class FootballField(name: String)


class BodySpec extends FertxTestBase {

  implicit val textMarshaller = new ResponseMarshaller[response.TextPlain, String] {
    override def handle(t: String, resp: HttpServerResponse): Unit =
      resp.end(t)
  }


  "Request body" should "be read simply" in {
    implicit val textUnmarshaller = new RequestUnmarshaller[TextPlain, String] {
      override def extract(rc: RoutingContext): Either[MalformedBody, String] =
        Right(rc.getBodyAsString.get)
    }
    val sent = "Some_payload"

    POST("api" / "echo")
      .accepts(RequestType.PLAIN_TEXT)
      .body[String]
      .produces(ResponseType.PLAIN_TEXT)
      .map(OK(_))

      .attachTo(router)

    startTest { () =>
      client.post("/api/echo")
        .putHeader("Content-Type", "text/plain")
        .sendBufferFuture(Buffer.buffer(sent))
        .map { response =>
          response.statusCode should be(200)
          response.bodyAsString should be(Some(sent))
        }
    }
  }

  "Valid request body" should "be read with JSON" in {
    val myOwnMapper = new ObjectMapper with ScalaObjectMapper
    myOwnMapper.registerModule(DefaultScalaModule)
    val anfield = FootballField("Anfield Road")
    implicit def jackson[T: Manifest]: RequestUnmarshaller[request.Json, T] = { rc =>
      try {
        Right(myOwnMapper.readValue[T](rc.getBodyAsString.get))
      } catch {
        case _:Exception => Left(new MalformedBody)
      }
    }

    POST("api" / "fields")
      .accepts(RequestType.JSON)
      .body[FootballField]
      .produces(ResponseType.PLAIN_TEXT)
      .map(field =>
        OK(field.name)
      )

      .attachTo(router)

    startTest { () =>
      client.post("/api/fields")
        .putHeader("Content-Type", "application/json")
        .sendBufferFuture(Buffer.buffer(myOwnMapper.writeValueAsString(anfield)))
        .map { response =>
          response.statusCode should be(200)
          response.bodyAsString should be(Some(anfield.name))
        }
    }
  }

  "Invalid request body" should "result in bad request" in {
    val myOwnMapper = new ObjectMapper with ScalaObjectMapper
    myOwnMapper.registerModule(DefaultScalaModule)
    implicit def jackson[T: Manifest]: RequestUnmarshaller[request.Json, T] = { rc =>
      try {
        Right(myOwnMapper.readValue[T](rc.getBodyAsString.get))
      } catch {
        case _:Exception => Left(new MalformedBody)
      }
    }

    POST("api" / "fields" / "invalid")
      .accepts(RequestType.JSON)
      .body[FootballField]
      .produces(ResponseType.PLAIN_TEXT)
      .map(field =>
        OK(field.name)
      )

      .attachTo(router)

    startTest { () =>
      client.post("/api/fields/invalid")
        .putHeader("Content-Type", "application/json")
        .sendBufferFuture(Buffer.buffer(""))
        .map { response =>
          response.statusCode should be(400)
        }
    }
  }

}
