package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl._
import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.dsl.path.PathDefinition
import com.github.aesteve.fertx.dsl.routing.impl.RequestReaderDefinition
import com.github.aesteve.fertx.util._

object TestPathDefinition extends App {

  // () => Response
  val OKUnit: () => Response = () => OK
  val simpleStrPath: PathDefinition[Unit] = "api"
  GET(simpleStrPath) { () =>
    OK
  }
  GET(simpleStrPath).mapUnit(OKUnit)
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
  val intParam: PathDefinition[Tuple1[Int]] = "api" / IntPath
  GET("api" / IntPath) { int =>
    assert(int.isInstanceOf[Int])
    OK
  }
  private val OKWithArity1Int = { int: Int => OK }
  GET(intParam)(OKWithArity1Int)
  private def okWith1Int(i: Int) = OK
  GET(intParam).map(okWith1Int)
  GET(intParam)(okWith1Int)


  // (Int, Int) => Response
  val oneStringTwoInts: PathDefinition[(Int, Int)] = "api" / IntPath / IntPath
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


  val pathAndQuery: RequestReaderDefinition[(Int, Int), (Int, Int, Int)] =
    GET("api" / IntPath / IntPath / *)
      .intQuery("someint") // mandatory

  pathAndQuery { (int1, int2, int3) =>
    assert(int1.isInstanceOf[Int])
    assert(int2.isInstanceOf[Int])
    assert(int3.isInstanceOf[Int])
    OK
  }

  val pathAndQueries: RequestReaderDefinition[Unit, (Int, String)] =
    GET("api" / "twoqueries")
      .intQuery("someint")
      .query("someMandatoryString")

  pathAndQueries.map { (int, str) =>
    assert(int.isInstanceOf[Int])
    assert(str.isInstanceOf[String])
    OK
  }

  val path = "api" / IntPath / IntPath
  GET(path).map { (int1, int2) =>
    assert(int1.isInstanceOf[Int])
    assert(int2.isInstanceOf[Int])
    OK
  }
  val f: (Int, Int) => Response = (int1, int2) => OK
  GET(path).map(f)

  val pathWithOptQuery = "api" / "path" / "opt" / "query"
  GET(pathWithOptQuery)
      .intQueryOpt("notMandatory")
      .map {
        case Some(_) => OK
        case None => NotFound
      }

  GET("api" / "path" / "2" / "opt" / "queries")
    .intQueryOpt("notMandatory1")
    .intQueryOpt("notMandatory2")
    .mapTuple {
      case (Some(_), Some(_))   => OK
      case (Some(_), None)      => OK
      case (None, Some(_))      => OK
      case (None, None)         => NotFound
    }

  GET("api" / "query" / "custom" / "withdefault")
    .tryQuery("test", _.map(_.toInt).getOrElse(0))
    .map { i: Int => OK }

  implicit def intMarshaller: ResponseMarshaller[Int] =
    _.toString

  GET("api" / "query" / "custom" / "withoutdefault")
    .tryQuery("test", _.map(_.toInt))
    .map(OkOrNotFound)
}
