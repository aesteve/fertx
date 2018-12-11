package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl._
import com.github.aesteve.fertx.media._
import com.github.aesteve.fertx.response.OK
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class PathTest extends FertxTestBase with SendsDefaultText {

  "A simple path" should "be accepted" in {
    GET("api" / "v1" / "health") { () =>
      OK
    }.attachTo(router)
    startTest { () =>
      client.get("/api/v1/health").sendFuture().flatMap { response =>
        response.statusCode should be(200)
        client.get("nonexisting").sendFuture().map { response2 =>
          response2.statusCode should be(404)
        }
      }
    }
  }

  "Path parameters" should "be extracted" in {
    val serverPath = "api" / StrPath / "toons" / IntPath
    val requestPath = "/api/v1/toons/3"
    requestPath should fullyMatch regex serverPath.toFullPath
    GET(serverPath)
      .produces[`text/plain`]
      .map { (apiVersion, toonId) =>
        OK(s"$apiVersion$toonId")
      }.attachTo(router)
    startTest { () =>
      client.get(requestPath).sendFuture().flatMap { response =>
        response.statusCode should be(200)
        response.bodyAsString() should be (Some("v13"))
        client.get("nonexisting").sendFuture().map { response2 =>
          response2.statusCode should be(404)
        }
      }
    }
  }

  "Wildcard path" should "be resolved" in {
    val serverPath = "api" / *
    val requestPath = "/api/v1/test/something"
    requestPath should fullyMatch regex serverPath.toFullPath
    GET(serverPath ) { () =>
      OK
    }.attachTo(router)
    startTest { () =>
      client.get(requestPath).sendFuture().flatMap { response =>
        response.statusCode should be(200)
        client.get("nonexisting").sendFuture().map { response2 =>
          response2.statusCode should be(404)
        }
      }
    }
  }

  "Wildcard path" should "be resolved in any position" in {
    val serverPath = "api" / * / "test" / *
    val requestPath = "/api/v1/test/something"
    requestPath should fullyMatch regex serverPath.toFullPath
    GET(serverPath ) { () =>
      OK
    }.attachTo(router)
    startTest { () =>
      client.get(requestPath).sendFuture().flatMap { response =>
        response.statusCode should be(200)
        client.get("nonexisting").sendFuture().map { response2 =>
          response2.statusCode should be(404)
        }
      }
    }
  }

}
