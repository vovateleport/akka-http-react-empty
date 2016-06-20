package sr.ui

import japgolly.scalajs.react._
import sr.model.TodoItem
import vdom.prefix_<^._

import scala.concurrent.ExecutionContext.Implicits.global

object TodoControl {
  val TodoList = ReactComponentB[Seq[TodoItem]]("TodoList")
    .render_P { props =>
      def createItem(item: TodoItem) = <.li(item.view())
      <.ul(props map createItem)
    }
    .build

  case class State(items: Seq[TodoItem], text: String)

  class Backend($: BackendScope[Unit, State]) {
    def onChange(e: ReactEventI) = {
      val newValue = e.target.value
      $.modState(_.copy(text = newValue))
    }

    def handleSubmit(e: ReactEventI) = Callback.future {
      e.preventDefault()
      val v = $.state.runNow().text
      AppService.addTodo(v).map(_ => $.modState(s => s.copy(text = ""), load()))
    }



    def render(state: State) =
      <.div(
        <.h3("TODO"),
        TodoList(state.items),
        <.form(^.onSubmit ==> handleSubmit,
          <.input(^.onChange ==> onChange, ^.value := state.text),
          <.button("Add #", state.items.length + 1)
        )
      )

    def load() = Callback.future {
      AppService.allTodos().map(data => $.modState(s => s.copy(items = data)))
    }
  }

  val component = ReactComponentB[Unit]("TodoControl")
    .initialState(State(Nil, ""))
    .renderBackend[Backend]
    .componentDidMount(_.backend.load())
    .build
}
