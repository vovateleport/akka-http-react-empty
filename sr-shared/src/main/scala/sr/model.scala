package sr

object model {
  case class TodoItem(message:String, timestamp:String) {
    def view() = s"$message [$timestamp]"
  }
}
