package com.github.aesteve.fertx

import com.github.aesteve.fertx.dsl._
import com.github.aesteve.fertx.dsl.extractors.Extractor
import com.github.aesteve.fertx.dsl.path.PathDef
import com.github.aesteve.fertx.dsl.routing.RouteDef
import com.github.aesteve.fertx.util._

object TestPathDefinition extends App {

  // () => Response
  val OKUnit: () => Response = () => OK
  val simpleStrPath: PathDef[Unit] = "api"
  GET(simpleStrPath) { () =>
    OK
  }
  GET(simpleStrPath).fold(OKUnit)
  GET(simpleStrPath)(OKUnit)

  private val OKWithArity0 = () => OK
  GET(simpleStrPath)(OKWithArity0)
  private def okWithUnit() = OK
  GET(simpleStrPath).fold(okWithUnit)
  GET(simpleStrPath)(okWithUnit)

  // () => Response
  val twoStrings: PathDef[Unit] = "api" / "v1"
  GET(twoStrings)(() => OK)
  GET(twoStrings)(OKWithArity0)
  GET(twoStrings).fold(okWithUnit)
  GET(twoStrings)(okWithUnit)


  // Int => Response
  val intParam: PathDef[Tuple1[Int]] = "api" / IntPath
  GET(intParam) { int =>
    assert(int.isInstanceOf[Int])
    OK
  }
  private val OKWithArity1Int = { int: Int => OK }
  GET(intParam)(OKWithArity1Int)
  private def okWith1Int(i: Int) = OK
  GET(intParam).fold(okWith1Int)
  GET(intParam)(okWith1Int)


  // (Int, Int) => Response
  val oneStringTwoInts: PathDef[(Int, Int)] = "api" / IntPath / IntPath
  GET(oneStringTwoInts) { (int1, int2) =>
    assert(int1.isInstanceOf[Int])
    assert(int2.isInstanceOf[Int])
    OK
  }
  private val OKWithArity2IntInt = { (i: Int, j: Int) => OK }
  GET(oneStringTwoInts)(OKWithArity2IntInt)
  private def okWith2Ints(i: Int, j: Int) = OK
  GET(oneStringTwoInts)(okWith2Ints)

  val wildcardPath: PathDef[Unit] = "api" / *
  GET(wildcardPath) { () => OK }

  println(wildcardPath.toFullPath)
  println(wildcardPath.extractor.isInstanceOf[Extractor[Unit]])


  val pathAndQuery: RouteDef[(Int, Int), (Int, Int, Int)] =
    GET("api" / IntPath / IntPath / *)
      .intQuery("someint") // mandatory

  pathAndQuery { (int1, int2, int3) =>
    assert(int1.isInstanceOf[Int])
    assert(int2.isInstanceOf[Int])
    assert(int3.isInstanceOf[Int])
    OK
  }

  val pathAndQueries: RouteDef[Unit, (Int, String)] =
    GET("api" / "twoqueries")
      .intQuery("someint")
      .query("someMandatoryString")

  pathAndQueries.fold { (int, str) =>
    assert(int.isInstanceOf[Int])
    assert(str.isInstanceOf[String])
    OK
  }

  val path = "api" / IntPath / IntPath
  GET(path).fold { (int1, int2) =>
    assert(int1.isInstanceOf[Int])
    assert(int2.isInstanceOf[Int])
    OK
  }
  val f: (Int, Int) => Response = (int1, int2) => OK
  GET(path).fold(f)

  val pathWithOptQuery = "api" / "path" / "opt" / "query"
  GET(pathWithOptQuery)
      .intQueryOpt("notMandatory")
      .fold {
        case Some(_) => OK
        case None => NotFound
      }

  GET("api" / "path" / "2" / "opt" / "queries")
    .intQueryOpt("notMandatory1")
    .intQueryOpt("notMandatory2")
    .fold {
      case (Some(_), Some(_))   => OK
      case (Some(_), None)      => OK
      case (None, Some(_))      => OK
      case (None, None)         => NotFound
    }

  GET("api" / "query" / "custom" / "withdefault")
    .tryQuery("test", _.map(_.toInt).getOrElse(0))
    .fold { i: Int => OK }

  GET("api" / "query" / "custom" / "withoutdefault")
    .tryQuery("test", _.map(_.toInt))
    .fold(OkOrNotFound)
}
