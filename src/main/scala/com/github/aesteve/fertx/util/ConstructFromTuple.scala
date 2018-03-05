package com.github.aesteve.fertx.util

/**
 * Constructor for instances of type `R` which can be created from a tuple of type `T`.
 */
trait ConstructFromTuple[T, R] extends (T ⇒ R)

object ConstructFromTuple extends ConstructFromTupleInstances
