package sr.ui

import japgolly.scalajs.react.ReactDOM
import org.scalajs.dom
import org.scalajs.dom._
import sr.Api
import sr.model.TodoItem
import upickle.Js
import upickle.default._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.JSApp

import autowire._

object App extends JSApp {
  /**
    * Main entry point
    */
  def main(): Unit = {
    ReactDOM.render(TodoControl.component(), document.getElementById("node0"))
  }
}

object AppService {
  val basePath = dom.document.location.origin.toOption.getOrElse("")
  val client = new Client(basePath)

  def addTodo(text: String): Future[Unit] = client[Api].addTodo(text).call()
  def allTodos(): Future[Seq[TodoItem]] = client[Api].allTodos().call()
}

class Client(val basePath:String) extends autowire.Client[Js.Value, Reader, Writer]{
  override def doCall(req: Request): Future[Js.Value] = {
    dom.ext.Ajax.post(
      url = s"$basePath/api/" + req.path.mkString("/"),
      data = upickle.json.write(Js.Obj(req.args.toSeq:_*))
    ).map(r => upickle.json.read(r.responseText))
  }

  def read[Result: Reader](p: Js.Value) = readJs[Result](p)
  def write[Result: Writer](r: Result) = writeJs(r)
}
