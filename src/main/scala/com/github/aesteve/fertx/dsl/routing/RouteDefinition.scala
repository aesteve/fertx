package com.github.aesteve.fertx.dsl.routing

import com.github.aesteve.fertx.request.RequestType
import com.github.aesteve.fertx.response.{ErrorMarshaller, ResponseType}

trait RouteDefinition[T, CurrentRequestType <: RequestType, CurrentResponseType <: ResponseType] extends SealableRoute[T, CurrentResponseType] {

  def produces[NewMime <: ResponseType](mimeType: NewMime)(implicit errorMarshaller: ErrorMarshaller[NewMime]): RouteDefinition[T, CurrentRequestType, NewMime]

}
