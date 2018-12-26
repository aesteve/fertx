package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl._
import com.github.aesteve.fertx.media._
import com.github.aesteve.fertx.response._

class QueryParamTest extends FertxTestBase with SendsDefaultText {

  "Mandatory query param" should "not be missing" in {
    route =
      GET("api" / "mandatoryparam")
        .mandatoryQuery("mandatoryparam")
        .produces[`text/plain`]
        .map { param =>
          OK(param)
        }

    startTest { () =>
      get("/api/mandatoryparam").sendFuture().map {
        _.statusCode should be (400)
      }
    }
  }

  "Mandatory query params" should "not be missing" in {
    val param1 = "mandatory1"
    val param2 = "mandatory2"
    val param1Val = "1st"
    val param2Val = "2nd"

    route =
      GET("api" / "mandatoryparams")
        .mandatoryQuery(param1)
        .mandatoryQuery(param2)
        .produces[`text/plain`]
        .map { (first, second) =>
          OK(s"$first:$second")
        }

    val path = "/api/mandatoryparams"
    startTest { () =>
      getNow(path).flatMap { respNoParam =>

          respNoParam.statusCode should be (400)

          get(path)
            .addQueryParam(param1, param1Val)
            .sendFuture()
            .flatMap { resp1Param =>

              resp1Param.statusCode should be (400)

              get(path)
                .addQueryParam(param2, param2Val)
                .sendFuture()
                .flatMap { resp2param =>

                  resp2param.statusCode should be(400)

                  get(path)
                    .addQueryParam(param1, param1Val)
                    .addQueryParam(param2, param2Val)
                    .sendFuture()
                    .map { bothParamsResp =>

                      bothParamsResp.statusCode should be (200)
                      bothParamsResp.bodyAsString should be (Some(s"$param1Val:$param2Val"))

                    }
              }
        }
      }
    }
  }

  "Non-Mandatory params" should "be missing without error" in {
    val paramName = "someparam"
    val paramValue = "someparamvalue"
    val path = "/api/nonmandatory"

    route =
      GET("api" / "nonmandatory")
        .optQuery(paramName)
        .produces[`text/plain`]
        .map {
          case Some(thing) => OK(thing)
          case None => NotFound
        }

    startTest { () =>
      getNow(path).flatMap { absentResp =>
        absentResp.statusCode should be(404)

        get(path)
          .addQueryParam(paramName, paramValue)
          .sendFuture()
          .map { presentResp =>
            presentResp.statusCode should be(200)
            presentResp.bodyAsString should be(Some(paramValue))
          }
      }
    }
  }

}
