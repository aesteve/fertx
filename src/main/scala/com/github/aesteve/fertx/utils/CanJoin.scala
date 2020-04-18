package com.github.aesteve.fertx.utils

trait CanJoin[A, B] {
  type Joined
  def join(a: A, b: B): Joined
}
