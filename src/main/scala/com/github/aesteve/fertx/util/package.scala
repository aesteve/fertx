package com.github.aesteve.fertx

package object util {

  def OkOrNotFound[T](payload: Option[T])(implicit marshaller: ResponseMarshaller[T]): Response =
    payload.map(OK(_)).getOrElse(NotFound)

  object Marshallers {

    implicit def intToStr: ResponseMarshaller[Int] =
      _.toString

    implicit def strToStr: ResponseMarshaller[String] =
      s => s

  }

  implicit class MyStringContext(val sc: StringContext) {

    object hs {

      def apply(args: Any*): String =
        sc.s(args)

      def unapplySeq(s: String): Option[Seq[String]] = {
        val regexp = sc.parts.mkString("(.+)").r
        regexp.unapplySeq(s)
      }

    }

  }

  /*
  implicit class HeaderUnapply(multiMap: MultiMap) {

    object Authorization {

      def unapply(multiMap: MultiMap): Option[String] = {
        val regexp = sc.parts.mkString("(.+)").r
        regexp.unapplySeq(s)
      }

    }
  }
  */

}
