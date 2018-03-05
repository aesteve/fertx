package com.github.aesteve.fertx.util

trait Tupler[T] {
  type Out
  def OutIsTuple: Tuple[Out]
  def apply(value: T): Out
}

object Tupler extends LowerPriorityTupler {
  implicit def forTuple[T: Tuple]: Tupler[T] { type Out = T } =
    new Tupler[T] {
      type Out = T
      def OutIsTuple = implicitly[Tuple[Out]]
      def apply(value: T) = value
    }
}

private[util] abstract class LowerPriorityTupler {
  implicit def forAnyRef[T]: Tupler[T] { type Out = Tuple1[T] } =
    new Tupler[T] {
      type Out = Tuple1[T]
      def OutIsTuple = implicitly[Tuple[Out]]
      def apply(value: T) = Tuple1(value)
    }
}
