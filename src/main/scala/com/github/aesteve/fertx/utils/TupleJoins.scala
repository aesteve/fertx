package com.github.aesteve.fertx.utils

object TupleJoins {
  given unit2Joiner as CanJoin[Unit, Unit] {
    override type Joined = Unit
    override def join(left: Unit, right: Unit): Joined = ()
  }
  given unitLeftJoin[T] as CanJoin[Unit, T] {
    override type Joined = T
    override def join(unit: Unit, t: T) = t
  }
  given unitRightJoin[T] as CanJoin[T, Unit] {
    override type Joined = T
    override def join(t: T, unit: Unit) = t
  }
  given tupleJoiner[A, B] as CanJoin[A, B] {
    override type Joined = (A, B)
    override def join(a: A, b: B) = (a, b)
  }
  given tuple2Joiner[A, B, C] as CanJoin[(A, B), C] {
    override type Joined = Tuple3[A, B, C]
    override def join(left: (A, B), right: C) = Tuple3(left._1, left._2, right)
  }
  given tuple3Joiner[A, B, C, D] as CanJoin[(A, B, C), D] {
    override type Joined = Tuple4[A, B, C, D]
    override def join(left: (A, B, C), right: D) = Tuple4(left._1, left._2, left._3, right)
  }
  given tuple4Joiner[A, B, C, D, E] as CanJoin[(A, B, C, D), E] {
    override type Joined = Tuple5[A, B, C, D, E]
    override def join(left: (A, B, C, D), right: E) = Tuple5(left._1, left._2, left._3, left._4, right)
  }
}
