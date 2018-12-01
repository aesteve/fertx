package com.github.aesteve.fertx.dsl.routing.impl

import com.github.aesteve.fertx.Response
import com.github.aesteve.fertx.dsl.routing.{FinalizedRoute, RouteDefinition, SealableRoute}

class RouteDefinitionImpl[Path, RequestPayload, MappedPayload](
  requestDef: RequestReaderDefinition[Path, RequestPayload],
  mapper: RequestPayload => MappedPayload,
) extends RouteDefinition[MappedPayload] {


  override def mapTuple(f: MappedPayload => Response): FinalizedRoute =
    new FinalizedRouteImpl(requestDef, mapper, f)
  override def mapUnit(f: () => Response): FinalizedRoute =
    new FinalizedUnitRouteImpl(requestDef, mapper, f)

  //def flatMap[T](mapping: MappedPayload => Future[T]): RouteDefinition[Path, RequestPayload, T] = ???


}
