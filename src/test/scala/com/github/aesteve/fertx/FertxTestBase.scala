package com.github.aesteve.fertx

import io.vertx.scala.core.Vertx
import io.vertx.scala.core.http.{HttpClient, HttpClientOptions, HttpServer, HttpServerOptions}
import io.vertx.scala.ext.web.Router
import io.vertx.scala.ext.web.client.{WebClient, WebClientOptions}
import org.scalatest.compatible.Assertion
import org.scalatest.{Assertions, AsyncFlatSpec, BeforeAndAfter, Matchers}

import scala.concurrent.Future

abstract class FertxTestBase extends AsyncFlatSpec with Matchers with Assertions with BeforeAndAfter {

  protected val Port = 9999
  protected val Host = "localhost"
  protected val ServerOptions = HttpServerOptions().setPort(Port).setHost(Host)
  protected val ClientOptions = WebClientOptions().setDefaultHost(Host).setDefaultPort(Port)

  protected var vertx: Vertx = _
  protected var server: HttpServer = _
  protected var router: Router = _
  protected var client: WebClient = _
  protected var httpClient: HttpClient = _

  before {
    vertx = Vertx.vertx
    server = vertx.createHttpServer(ServerOptions)
    router = Router.router(vertx)
    client = WebClient.create(vertx, ClientOptions)
  }

  after {
    vertx.close()
  }

  def startTest(realTest: () => Future[Assertion]): Future[Assertion] =
    server
      .requestHandler(router.accept)
      .listenFuture()
      .flatMap[Assertion](_ => realTest())

}
