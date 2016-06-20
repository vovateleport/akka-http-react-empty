package sr.service

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer

import scala.concurrent.ExecutionContextExecutor

trait ServiceContext {
  implicit val system: ActorSystem
  implicit val executor: ExecutionContextExecutor
  implicit val materializer: Materializer
}

trait Service extends ServiceContext with ApiService{
  val root: Route =
    path("hello") {
      get { complete("Hello!") } //just for check
    } ~ pathPrefix("api") {
      routeApi
    } ~ pathPrefix("ui") {
      getFromResourceDirectory("")
    } ~ path("") {
      redirect("ui/index.html", StatusCodes.MovedPermanently)
    }
}