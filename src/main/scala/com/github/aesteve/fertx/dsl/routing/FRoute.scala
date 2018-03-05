package com.github.aesteve.fertx.dsl.routing

import com.github.aesteve.fertx.Response
import com.github.aesteve.fertx.dsl.extractors.Extractor

class FRoute[A, B, C](routeDef: RouteDef[A, B], payloadExtractor: Extractor[C]) {

  def map[T](mapping: C => T): FRoute[A, B, T] = ???

  def fold(folder: C => Response): Unit = {}//???
  def fold(folder: () => Response): Unit = {} //???

}
