package sr.service

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.typesafe.config.ConfigFactory
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object SelfHosted {
  def main(args: Array[String]) {
    val host = "localhost"
    val port = 8008
    // we need an ActorSystem to host our application in
    implicit val system = ActorSystem("system")

    // create and start our service actor
    val service = system.actorOf(Props[ServiceActor], "sr-service")

    implicit val timeout = Timeout(5.seconds)

    // start a new HTTP server with our service actor as the handler
    IO(Http) ? Http.Bind(service, interface = host, port = port)

    println(s"Listen connections on [$host:$port].\nPress Enter to terminate.")
    val result = scala.io.StdIn.readLine()
    system.terminate()
  }
}