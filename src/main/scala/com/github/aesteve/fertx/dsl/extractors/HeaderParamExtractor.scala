package com.github.aesteve.fertx.dsl.extractors

import com.github.aesteve.fertx.response.ClientError
import io.vertx.scala.ext.web.RoutingContext


abstract class HeaderParamExtractor[T](name: String) extends Extractor[T] {


  def fromReq: Option[String] => Either[ClientError, T]

  override def getFromContext: RoutingContext => Either[ClientError, T] =
    rc => fromReq(rc.request.getHeader(name))

}
/*
case class StrHeader(name: String) extends HeaderParamExtractor[String](name) {
  override def fromReq: Option[String] => Either[ClientError, String] =
    s => s match {
      case Some(value) => value
      case _ => BadRequest(s"Header $name not specified")
    }

}
*/
/*
class HeaderParamExtractor2[T](ext: MultiMatcher[HttpHeader, T]) extends Extractor[T] {

  override def getFromContext: RoutingContext => Either[ClientError, T] =
    rc =>
        ext.apply(rc.request.headers)

}


class test {

  def getBearerToken: PartialFunction[HttpHeader, String] = {
    case Authorization(h"Bearer $token") => token
  }



}

*/
