/*
 * Copyright (C) 2009-2017 Lightbend Inc. <http://www.lightbend.com>
 */

package com.github.aesteve.fertx.util

import com.github.aesteve.fertx.Response

/**
 * ApplyConverter allows generic conversion of functions of type
  * `(T1, T2, ...) => Route` to `(TupleX(T1, T2, ...)) => Route`.
  *
  *
  *
  *
 */
abstract class ApplyConverter[L] {
  type In
  def apply(f: In): L ⇒ Response
}

object ApplyConverter extends ApplyConverterInstances
