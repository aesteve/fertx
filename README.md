# Fertx: Functional Vert.x Web DSL

Fertx is a functional way of dealing with Vert.x web's Route declaration.

### Minimalistic example:

```scala
HEAD("api" / "health").map { () =>
  OK
}
```
`map` can be replaced by `apply` if you find it more natural. The example above could be written:
```scala
HEAD("api" / "health") { () =>
  OK
}
```


### Extract data from path:

```scala 
GET("api" / "todos" / IntPath) { todoId =>
  if (todoId > 10) { // todoId is an Int
    NotFound
  } else {
    OK
  }
}
```

### Extract data from path and query:
```scala
GET("api" / "todos" / IntPath)
  .query("filterById")
  .map { (todoId, paramValue) =>
    // ...
  }
}
```

### Return response body:
Let's try:
```scala
case class Todo(id: Int)
GET("api" / "todos" / IntPath)
  .produces(ResponseType.PLAIN_TEXT)
  .map { todoId => 
    OK(Todo(todoId))   
  }
```
The compiler will complain he cannot find neither a `ResponseMarshaller[TextPlain, Todo]` nor an `ErrorMarshaller[TextPlain]`
This is the way to ensure, at compile time, that you're not using "unmarshallable" objects.
And indeed, what should a `Todo` instance look like once sent to `text/plain` ?

```scala
case class Todo(id: Int)
// you've defined a proper way to marshall a Todo instance to plain text
implicit val todoTextWriter = new ResponseMarshaller[TextPlain, Todo] {
  override def handle(todo: Todo, resp: HttpServerResponse): Unit =
    resp.end(todo.id.toString)
} 
// you also have to provide a way to handle errors in plain/text, you can use fertx built-in marshaller
import com.github.aesteve.dsl.marshallers.SimpleErrorTextMarshaller 
GET("api" / "todos" / IntPath)
  .produces(ResponseType.PLAIN_TEXT)
  .map { todoId => 
    OK(Todo(todoId))   
  }
```
Now compiles.
You can deal with your domain objects in a safe way.
You can also choose the strategy you wan for marshalling within your application. Either "one per domain class", or a "generic one for every object".
Also, you can scope the `implicit` wherever you want. Either a global import for all your routes, or like in the example, very close to the route definition.

### Read request body:

The same way, we have to define a `RequestUnmarshaller[request.TextPlain, Todo]`.
```scala
case class Todo(id: Int)
implicit val todoStrUnmarshaller = new RequestUnmarshaller[request.TextPlain, Todo] {
  override def extract(rc: RoutingContext): Either[MalformedBody, Payload] =
    try {
      Right(rc.getBodyAsString.get.toInt)
    } catch {
      case _: Exception => Left(new MalformedBody)
    }
}
POST("api" / "todos")
  .accepts(RequesType.PLAIN_TEXT)
  .body[Todo] // no problem, since the Unmarshaller is in the scope
  .map { todo => 
    // save it or whatever
    Accepted // doesn't even need to mention "produces" since we're not producing any content
  }

```

### Deal with asynchronous responses

```scala
def longComputation(): Future[Long] = ???
POST("api" / "compute")
 .produces(ResponseType.PLAIN_TEXT) // given we have a ResponseMarshaller[TextPlain, Long] in scope
 .flatMap { () =>
  longComputation().map(OK(_)) 
 }
```
