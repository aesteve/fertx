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
    val serverPath = "api" / 'version / "toons" / 'id
    val requestPath = "/api/v1/toons/3"

    route =
      GET(serverPath)
        .produces[`text/plain`]
        .map { (apiVersion, toonId) =>
          OK(s"$apiVersion/$toonId")
        }

    startTest { () =>
      getNow(requestPath).flatMap { response =>
        response.statusCode should be(200)
        response.bodyAsString() should be (Some("v1/3"))

        getNow("nonexisting").map { response2 =>
          response2.statusCode should be(404)
        }
      }
    }
  }

  "Wildcard path" should "be resolved" in {
    val serverPath = "api" / *
    val requestPath = "/api/v1/test/something"

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
    val serverPath = "api" / "v1" / "test" / *
    val requestPath = "/api/v1/test/something"

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

  "Different types of path" should "be extracted" in {

    val serverPath =
      "api" /
      'string /
      'short.as[Short] /
      'int.as[Int] /
      'long.as[Long] /
      'double.as[Double] /
      'float.as[Float] /
      'bool.as[Boolean]

    val requestPath = "/api/str/1/2049/999999999999/2.0/7.0/true"

    route =
      GET(serverPath)
        .produces[`text/plain`]
        .map { (str, short, int, long, double, float, bool) =>
          OK(s"/api/$str/$short/$int/$long/$double/$float/$bool")
        }

    startTest { () =>
      getNow(requestPath).flatMap { response =>
        response.statusCode should be(200)
        response.bodyAsString should be(Some(requestPath))
      }
    }

  }

}
