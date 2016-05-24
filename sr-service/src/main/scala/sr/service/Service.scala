package sr.service

import spray.http.StatusCodes._
import spray.routing._

trait Service extends HttpService with ApiService{
  val root: Route =
    path("hello") {
      get { complete("Hello!") } //just for check
    } ~ pathPrefix("api") {
      routeApi
    } ~ pathPrefix("ui") {
      getFromResourceDirectory("")
    } ~ path("") {
      redirect("ui/index.html", MovedPermanently)
    }
}