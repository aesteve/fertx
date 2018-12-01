/*
 * Copyright (C) 2009-2017 Lightbend Inc. <http://www.lightbend.com>
 */

package com.github.aesteve.fertx.util.applyconverters

import com.github.aesteve.fertx.dsl.routing.RouteDefinition

/**
 * ApplyConverter allows generic conversion of functions of type
  * `(T1, T2, ...) => Route` to `(TupleX(T1, T2, ...)) => Route`.
 */
abstract class ApplyConverter[L, R] {
  type In
  type Out = R
  def apply(f: In): L ⇒ R
}

object ApplyConverter extends ApplyConverterInstances
