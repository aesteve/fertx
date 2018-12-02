package com.github.aesteve.fertx.request

import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.response.{ClientError, MalformedBody}
import io.vertx.core.http.HttpMethod
import io.vertx.scala.ext.web.RoutingContext

trait Request {
  val path: String
  val method: HttpMethod
}

trait RequestUnmarshaller[Mime, Payload] extends Extractor[Tuple1[Payload]] {

  override def needsBody: Boolean = true

  def extract(rc: RoutingContext): Either[MalformedBody, Payload]

  override def getFromContext: RoutingContext => Either[ClientError, Tuple1[Payload]] =
    rc =>
      extract(rc).map(Tuple1(_))

}
