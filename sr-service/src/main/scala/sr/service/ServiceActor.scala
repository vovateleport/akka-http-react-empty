package sr.service

import akka.actor.{Actor, ActorLogging}
import spray.http.StatusCodes._
import spray.routing.ExceptionHandler
import spray.util.LoggingContext

class ServiceActor extends Actor with Service with ActorLogging  {

  implicit def exceptionHandler(implicit log: LoggingContext) =
    ExceptionHandler {
      // Intepcept ExceptionInInitializerError to prevent actor system from shutting down.
      case e: ExceptionInInitializerError =>
        log.error(e, s"Unhandled exception in initializer: $e")
        complete(InternalServerError, e.getMessage)
      case e: Error =>
        log.error(e, s"User exception: $e")
        complete(InternalServerError, e.getMessage)
    }

  def actorRefFactory = context

  def receive = runRoute(root)
}
