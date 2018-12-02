package com.github.aesteve.fertx.dsl.routing

import com.github.aesteve.fertx.ResponseType

trait RouteDefinition[T, CurrentMimeType <: ResponseType] extends SealableRoute[T, CurrentMimeType] {
  def produces[NewMime <: ResponseType](mimeType: NewMime): RouteDefinition[T, NewMime]
}
