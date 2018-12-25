package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl.routing.{FinalizedRoute, SealableRoute}
import io.vertx.core.buffer.Buffer
import io.vertx.scala.core.Vertx
import io.vertx.scala.core.http.{HttpClient, HttpServer, HttpServerOptions}
import io.vertx.scala.ext.web.Router
import io.vertx.scala.ext.web.client.{HttpRequest, HttpResponse, WebClient, WebClientOptions}
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

  protected var route: FinalizedRoute = _

  before {
    vertx = Vertx.vertx
    server = vertx.createHttpServer(ServerOptions)
    router = Router.router(vertx)
    client = WebClient.create(vertx, ClientOptions)
  }

  after {
    vertx.close()
  }

  def startTest(realTest: () => Future[Assertion]): Future[Assertion] = {
    route.attachTo(router)
    server
      .requestHandler(router.accept)
      .listenFuture()
      .flatMap[Assertion](_ => realTest())
  }

  def get(path: String): HttpRequest[Buffer] =
    client.get(path)

  def getNow(path: String): Future[HttpResponse[Buffer]] =
    client.get(path).sendFuture()

  def post(path: String): HttpRequest[Buffer] =
    client.post(path)

}
