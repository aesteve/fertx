package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl._
import com.github.aesteve.fertx.media._
import com.github.aesteve.fertx.response.OK

class PathTest extends FertxTestBase with SendsDefaultText {

  "A simple path" should "be accepted" in {
    route =
      GET("api" / "v1" / "health") { () =>
        OK
      }

    startTest { () =>
      getNow("/api/v1/health").flatMap { response =>
        response.statusCode should be(200)

        getNow("nonexisting").map { response2 =>
          response2.statusCode should be(404)
        }
      }
    }
  }

  "Path parameters" should "be extracted" in {
    val serverPath = "api" / StrPath / "toons" / IntPath
    val requestPath = "/api/v1/toons/3"
    requestPath should fullyMatch regex serverPath.toFullPath

    route =
      GET(serverPath)
        .produces[`text/plain`]
        .map { (apiVersion, toonId) =>
          OK(s"$apiVersion$toonId")
        }

    startTest { () =>
      getNow(requestPath).flatMap { response =>
        response.statusCode should be(200)
        response.bodyAsString() should be (Some("v13"))

        getNow("nonexisting").map { response2 =>
          response2.statusCode should be(404)
        }
      }
    }
  }

  "Wildcard path" should "be resolved" in {
    val serverPath = "api" / *
    val requestPath = "/api/v1/test/something"
    requestPath should fullyMatch regex serverPath.toFullPath

    route =
      GET(serverPath) { () =>
        OK
      }

    startTest { () =>
      getNow(requestPath).flatMap { response =>
        response.statusCode should be(200)

        getNow("nonexisting").map { response2 =>
          response2.statusCode should be(404)
        }
      }
    }
  }

  "Wildcard path" should "be resolved in any position" in {
    val serverPath = "api" / * / "test" / *
    val requestPath = "/api/v1/test/something"
    requestPath should fullyMatch regex serverPath.toFullPath

    route =
      GET(serverPath) { () =>
        OK
      }

    startTest { () =>
      getNow(requestPath).flatMap { response =>
        response.statusCode should be(200)

        getNow("nonexisting").map { response2 =>
          response2.statusCode should be(404)
        }
      }
    }
  }

}
