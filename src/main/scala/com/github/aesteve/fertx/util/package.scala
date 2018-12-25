package com.github.aesteve.fertx

import com.github.aesteve.fertx.media._
import com.github.aesteve.fertx.response.ResponseMarshaller

package object util {

  object PlainTextMarshallers {

    implicit def intToStr: ResponseMarshaller[`text/plain`, Int] =
      (i, resp) => resp.end(i.toString)

    implicit def strToStr: ResponseMarshaller[`text/plain`, String] =
      (str, resp) => resp.end(str)

  }

  /*

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
