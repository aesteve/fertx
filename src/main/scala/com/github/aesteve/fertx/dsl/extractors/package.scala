package com.github.aesteve.fertx.dsl

package object extractors {


  implicit class MyStringContext(val sc: StringContext) {

    object h {
      def apply(args: Any*): String =
        sc.s(args)

      def unapplySeq(s: String): Option[Seq[String]] = {
        val regexp = sc.parts.mkString("(.+)").r
        regexp.unapplySeq(s)
      }
    }

  }

  /*

  class MultiMatcher[A, B, C, D](a: A, extractor: B => C, converter: A => C => D) {

    def apply(pfs: PartialFunction[B, D]*): Seq[D] = {
      pfs.map { pf =>
        pf.lift.apply { b: B =>
          converter(a)(extractor(b))
        }
      }.collect{ case Some(v) => v }
    }

  }

  implicit def toMultiMatcher(multiMap: MultiMap): MultiMatcher[MultiMap, HttpHeader, String, Option[String]] = {
    val b: HttpHeader => String = { _.name }
    val a: MultiMap => String => Option[String] = { _.get }
    new MultiMatcher[MultiMap, HttpHeader, String, Option[String]](multiMap, b, a)
  }


  class Matched[T](val f: Function[RoutingContext, Either[ClientError, T]]) extends Function[RoutingContext, Either[ClientError, T]] {

    override def apply(v1: RoutingContext): Either[ClientError, T] =
      f.apply(v1)

    def |>[R](g: Function[T, Either[ClientError, R]]): Matched[R] =
      new Matched({ rc => f(rc).flatMap(g) })

  }

  class PartialMatcher[T](extractor: Extractor, pf: PartialFunction[Option[String], Either[ClientError, T]]) extends PartialFunction[RoutingContext, Either[ClientError, T]] {

    def ||(clientError: ClientError): Matched[T] =
      new Matched({ rc =>
        pf.applyOrElse(extractor(rc), _ => Left(clientError))
      })

    override def isDefinedAt(rc: RoutingContext): Boolean =
      pf.isDefinedAt(extractor.fromCtx(rc))

    override def apply(rc: RoutingContext): Either[ClientError, T] =
      (extractor.fromCtx andThen pf)(rc)

  }


  abstract class Extractor {

    def fromCtx: RoutingContext => Option[String]

    def `match`[T](pf: PartialFunction[Option[String], Either[ClientError, T]]): PartialMatcher[T] = {
      new PartialMatcher(this, pf)
    }

    def `match`[T](f: Function[Option[String], Either[ClientError, T]]): Matched[T] =
      new Matched(fromCtx andThen f)

  }

  case class Header(name: String) extends Extractor {

    override def fromCtx: RoutingContext => Option[String] =
      rc => rc.request.getHeader(name)

  }

  case class Query(name: String) extends Extractor {

    override def fromCtx: RoutingContext => Option[String] =
      rc => rc.request.getParam(name)

  }


  object test {

    implicit class RichFun[T](fun: Function[RoutingContext, Either[ClientError, T]]) {
      def andThen[R](fun2: Function[T, R]): Function[RoutingContext, Option[R]] =
        rc => fun(rc) match {
          case Left(error) =>
            rc.fail(500) // FIXME
            None
          case Right(payload) => Some(fun2(payload))
        }
    }

    case class User(token: String) {

      def canUpdateResource: Boolean =
        false

    }

    def checkToken(token: String): Either[ClientError, User] =
      if (token == "magic")
          Right(User(token))
      else
          Left(Forbidden("Unknown user"))

    def headerAuth =
      Header("Authorization") `match` {
        case Some(h"Bearer $token") => checkToken(token)
      }

    def queryAuth =
      Query("token") `match` {
        case Some(token: String) => checkToken(token)
      }

    def findToken = headerAuth orElse queryAuth


    def authorise = { user: User =>
      if (user.canUpdateResource)
        Right(user)
      else
        Left(Forbidden("Permission Denied"))
    }

    def authChain = findToken andThen {
      case Right(u) => authorise(u)
    }




  }

  */
}
