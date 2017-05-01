package httpservices

import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives.{complete, path, _}
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import common.AirportSystem

import scala.concurrent.duration.DurationInt

class AirportHttpService extends AirportSystem {
  implicit val timeout = Timeout(10 seconds)
  override val logger: LoggingAdapter = Logging(system, getClass)

  val route: Route =
    path("airports") {
        get {
          parameter("q") { (query) =>
            logger.info(s"Querying airports with:$query")
            complete(OK)
          }
        }
    }
}