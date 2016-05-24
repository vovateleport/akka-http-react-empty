package sr

import sr.model.TodoItem
import scala.concurrent.Future

trait Api {
  def addTodo(text: String): Unit
  def allTodos(): Seq[TodoItem]
}
