package com.github.aesteve.fertx

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.github.aesteve.fertx.dsl._
import com.github.aesteve.fertx.request.{RequestType, RequestUnmarshaller, TextPlain}
import com.github.aesteve.fertx.response._
import com.timeout.docless.schema.{JsonSchema, Primitives}
import io.vertx.core.buffer.Buffer
import io.vertx.scala.ext.web.RoutingContext


case class FootballField(name: String)


class BodySpec extends FertxTestBase with SendsDefaultText with Primitives {

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
    implicit val footballSchema = JsonSchema.deriveFor[FootballField]
    implicit def jackson[T: Manifest]: RequestUnmarshaller[request.Json, T] = (rc: RoutingContext) =>
      try {
        Right(myOwnMapper.readValue[T](rc.getBodyAsString.get))
      } catch {
        case _: Exception => Left(new MalformedBody)
      }
    val route =
      POST("api" / "fields")
        .accepts(RequestType.JSON)
        .produces(ResponseType.PLAIN_TEXT)
        .body[FootballField]
        .map(field =>
          OK(field.name)
        )

    route.attachTo(router)

    println(route.openAPIOperation)

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
    implicit val footballSchema = JsonSchema.deriveFor[FootballField]
    implicit def jackson[T: Manifest]: RequestUnmarshaller[request.Json, T] = (rc: RoutingContext) => try {
      Right(myOwnMapper.readValue[T](rc.getBodyAsString.get))
    } catch {
      case _: Exception => Left(new MalformedBody)
    }
    POST("api" / "fields" / "invalid")
      .accepts(RequestType.JSON)
      .produces(ResponseType.PLAIN_TEXT)
      .body[FootballField]
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
          response.bodyAsString should equal(Some("Invalid request body"))
        }
    }
  }

  "Both body and params" should "both be accessible" in {
    val sent = "something"
    val path = "42"
    val queryParam = "qparam"
    val queryValue = "qvalue"

    implicit val textUnmarshaller = new RequestUnmarshaller[TextPlain, String] {
      override def extract(rc: RoutingContext): Either[MalformedBody, String] =
        Right(rc.getBodyAsString.get)
    }

    POST("api" / "bodyandparams" / IntPath)
      .accepts(RequestType.PLAIN_TEXT)
      .produces(ResponseType.PLAIN_TEXT)
      .query(queryParam)
      .body[String]
      .map { (id, query, body) =>
        assert(id.isInstanceOf[Int])
        assert(query.isInstanceOf[String])
        assert(body.isInstanceOf[String])
        OK(s"$id:$query:$body")
      }
      .attachTo(router)

    startTest { () =>
      client.post(s"/api/bodyandparams/$path")
        .addQueryParam(queryParam, queryValue)
        .putHeader("Content-Type", "text/plain")
        .sendBufferFuture(Buffer.buffer(sent))
        .map { resp =>
          resp.statusCode should be(200)
          resp.bodyAsString.get should equal(s"$path:$queryValue:$sent")
        }
    }
  }

}
