package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl._
import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.dsl.path.PathDefinition
import com.github.aesteve.fertx.response._

object TestPathDefinition extends App with SendsDefaultText {

  // For compilation-check purpose only
  // TODO: Add real-life test matching these

  // () => Response
  val OKUnit: () => Response[Unit] = () => OK
  val simpleStrPath: PathDefinition[Unit] = "api"
  GET(simpleStrPath) { () =>
    OK
  }
  GET(simpleStrPath).map(OKUnit)
  GET(simpleStrPath)(OKUnit)

  private val OKWithArity0 = () => OK
  GET(simpleStrPath)(OKWithArity0)
  private def okWithUnit() = OK
  GET(simpleStrPath).map(okWithUnit)
  GET(simpleStrPath)(okWithUnit)

  // () => Response
  val twoStrings: PathDefinition[Unit] = "api" / "v1"
  GET(twoStrings)(() => OK)
  GET(twoStrings)(OKWithArity0)
  GET(twoStrings).map(okWithUnit)
  GET(twoStrings)(okWithUnit)


  // Int => Response
  val intParam: PathDefinition[Tuple1[Int]] = "api" / 'version.as[Int]
  GET("api" / 'version.as[Int]) { int =>
    assert(int.isInstanceOf[Int])
    OK
  }
  private val OKWithArity1Int = { int: Int => OK }
  GET(intParam)(OKWithArity1Int)
  private def okWith1Int(i: Int) = OK
  GET(intParam).map(okWith1Int)
  GET(intParam)(okWith1Int)


  // (Int, Int) => Response
  val oneStringTwoInts: PathDefinition[(Int, Int)] = "api" / 'i1.as[Int] / 'i2.as[Int]
  GET(oneStringTwoInts) { (int1, int2) =>
    assert(int1.isInstanceOf[Int])
    assert(int2.isInstanceOf[Int])
    OK
  }
  private val OKWithArity2IntInt = { (i: Int, j: Int) => OK }
  GET(oneStringTwoInts)(OKWithArity2IntInt)
  private def okWith2Ints(i: Int, j: Int) = OK
  GET(oneStringTwoInts)(okWith2Ints)

  val wildcardPath: PathDefinition[Unit] = "api" / *
  GET(wildcardPath) { () => OK }

  println(wildcardPath.toFullPath)
  println(wildcardPath.extractor.isInstanceOf[Extractor[Unit]])


  val pathAndQuery =
    GET("api" / 'i1.as[Int] / 'i2.as[Int] / *)
      .mandatoryQuery("someint") // mandatory

  pathAndQuery { (int1, int2, int3) =>
    assert(int1.isInstanceOf[Int])
    assert(int2.isInstanceOf[Int])
    assert(int3.isInstanceOf[String])
    OK
  }

  val pathAndQueries =
    GET("api" / "twoqueries")
      .mandatoryQuery("someint")
      .mandatoryQuery("someMandatoryString")

  pathAndQueries.map { (int, str) =>
    assert(int.isInstanceOf[String])
    assert(str.isInstanceOf[String])
    OK
  }

  val path = "api" / 'i1.as[Int] / 'i2.as[Int]
  GET(path).map { (int1, int2) =>
    assert(int1.isInstanceOf[Int])
    assert(int2.isInstanceOf[Int])
    OK
  }
  val f: (Int, Int) => Response[Unit] = (int1, int2) => OK
  GET(path).map(f)

  val pathWithOptQuery = "api" / "path" / "opt" / "query"
  GET(pathWithOptQuery)
      .optQuery("notMandatory")
      .map {
        case Some(_) => OK
        case None => NotFound
      }

  GET("api" / "path" / "2" / "opt" / "queries")
    .optQuery("notMandatory1")
    .optQuery("notMandatory2")
    .map {
      case (Some(_), Some(_))   => OK
      case (Some(_), None)      => OK
      case (None, Some(_))      => OK
      case (None, None)         => NotFound
    }


  /*

  val intSchema = new Schema[Int]()
  intSchema.`type`("integer")
  GET("api" / "query" / "custom" / "withdefault")
    .tryQuery("test", _.map(_.toInt).getOrElse(0), intSchema)
    .map { i: Int => OK }

  val IntToTextMarshaller: ResponseMarshaller[TextPlain, Int] =
    (int, resp) => resp.end(int.toString)

  GET("api" / "query" / "custom" / "withoutdefault")
    .tryQuery[Option[Int]]("test", _.map(_.toInt), intSchema)
    .produces(ResponseType.PLAIN_TEXT)
    .map {
      case Some(int) => OK(int)(IntToTextMarshaller)
      case None => NotFound
    }

  */

}
