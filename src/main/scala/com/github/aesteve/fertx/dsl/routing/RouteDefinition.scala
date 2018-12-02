package com.github.aesteve.fertx.dsl.routing

import com.github.aesteve.fertx.MimeType

trait RouteDefinition[T, CurrentMimeType <: MimeType] extends SealableRoute[T, CurrentMimeType] {
  def produces[NewMime <: MimeType](mimeType: NewMime): RouteDefinition[T, NewMime]
}
