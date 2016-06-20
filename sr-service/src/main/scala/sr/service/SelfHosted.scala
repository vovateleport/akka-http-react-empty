package sr.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer


object SelfHosted extends App with Service {
  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val interface = "localhost"
  val port = 8008

  val bindingFuture = Http().bindAndHandle(root, interface, port)

  println(s"Listening on $interface[$port]. Press Enter to stop.")
  val result = scala.io.StdIn.readLine()
  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
