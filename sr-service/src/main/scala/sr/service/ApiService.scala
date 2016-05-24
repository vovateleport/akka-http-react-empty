package sr.service

import java.time.LocalDateTime

import spray.routing._
import upickle.default._
import upickle.Js

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import sr.model.TodoItem
import autowire._

trait ApiService extends HttpService {
  lazy val routeApi: Route = post {
    path(Segments) { s =>
      extract(_.request.entity.asString) { e =>
        complete {
          import scala.concurrent.ExecutionContext.Implicits.global

          AutowireServer.route[sr.Api](ApiImpl)(
            autowire.Core.Request(s, upickle.json.read(e).asInstanceOf[Js.Obj].value.toMap)
          ).map(upickle.json.write(_, 0))
        }
      }
    }
  }
}

object ApiImpl extends sr.Api {
  var data:Seq[TodoItem] = Seq.empty

  def addTodo(text: String): Unit = {
    data = data :+ TodoItem(text, LocalDateTime.now().toLocalTime.toString)
  }

  def allTodos(): Seq[TodoItem] = data
 }

object AutowireServer extends autowire.Server[Js.Value, Reader, Writer] {
  def read[Result: Reader](p: Js.Value) = upickle.default.readJs[Result](p)
  def write[Result: Writer](r: Result) = upickle.default.writeJs(r)
}
