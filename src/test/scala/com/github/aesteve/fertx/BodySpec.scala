package com.github.aesteve.fertx

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.github.aesteve.fertx.dsl._
import com.github.aesteve.fertx.media._
import com.github.aesteve.fertx.request.RequestUnmarshaller
import com.github.aesteve.fertx.response._
import io.swagger.v3.oas.models.media.{Schema, StringSchema}
import io.vertx.core.buffer.Buffer
import io.vertx.scala.ext.web.RoutingContext

case class FootballField(name: String)

class BodySpec extends FertxTestBase with SendsDefaultText {

  "Request body" should "be read simply" in {
    implicit val textUnmarshaller = new RequestUnmarshaller[`text/plain`, String] {
      override def extract(rc: RoutingContext): Either[MalformedBody, String] =
        Right(rc.getBodyAsString.get)

      override def schema: Schema[String] =
        new StringSchema

    }
    val sent = "Some_payload"

    route =
      POST("api" / "echo")
        .accepts[`text/plain`]
        .body[String]
        .produces[`text/plain`]
        .map(OK(_))

    startTest { () =>
      post("/api/echo")
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
    implicit def jackson[T: Manifest] = new RequestUnmarshaller[`application/json`, T] {
      override def extract(rc: RoutingContext): Either[MalformedBody, T] =
        try {
          Right(myOwnMapper.readValue[T](rc.getBodyAsString.get))
        } catch {
          case e:Exception =>
            e.printStackTrace()
            Left(new MalformedBody)
        }

      override def schema: Schema[T] =
        ???
    }

    route =
      POST("api" / "fields")
        .accepts[`application/json`]
        .produces[`text/plain`]
        .body[FootballField]
        .map(field =>
          OK(field.name)
        )

    startTest { () =>
      post("/api/fields")
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
    implicit def jackson[T: Manifest] = new RequestUnmarshaller[`application/json`, T] {
      override def extract(rc: RoutingContext): Either[MalformedBody, T] =
        try {
          Right(myOwnMapper.readValue[T](rc.getBodyAsString.get))
        } catch {
          case _:Exception => Left(new MalformedBody)
        }

      override def schema: Schema[T] = ???
    }

    route =
      POST("api" / "fields" / "invalid")
        .accepts[`application/json`]
        .produces[`text/plain`]
        .body[FootballField]
        .map(field =>
          OK(field.name)
        )

    startTest { () =>
      post("/api/fields/invalid")
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

    implicit val textUnmarshaller = new RequestUnmarshaller[`text/plain`, String] {
      override def extract(rc: RoutingContext): Either[MalformedBody, String] =
        Right(rc.getBodyAsString.get)

      override def schema: Schema[String] = new StringSchema
    }

    route =
      POST("api" / "bodyandparams" / 'id.as[Int])
        .accepts[`text/plain`]
        .produces[`text/plain`]
        .mandatoryQuery(queryParam)
        .body[String]
        .map { (id, query, body) =>
          assert(id.isInstanceOf[Int])
          assert(query.isInstanceOf[String])
          assert(body.isInstanceOf[String])
          OK(s"$id:$query:$body")
        }

    startTest { () =>
      post(s"/api/bodyandparams/$path")
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
