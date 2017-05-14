package httpservices

import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.HttpEntity
import akka.http.scaladsl.model.StatusCodes.{NotFound, OK}
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives.{complete, path, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.util.Timeout
import com.google.inject.{Inject, Singleton}
import common.AirportSystem
import play.api.libs.json.{JsObject, Json}
import services.{AirportService, CountryService}
import spray.json.DefaultJsonProtocol

import scala.concurrent.duration.DurationInt

trait Protocols extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val um: Unmarshaller[HttpEntity, JsObject] = {
    Unmarshaller.byteStringUnmarshaller.mapWithCharset { (data, charset) =>
      Json.parse(data.toArray).as[JsObject]
    }
  }
}

@Singleton
class AirportHttpService @Inject()(airportService: AirportService,
                                   countryService: CountryService) extends AirportSystem with Protocols {
  implicit val timeout = Timeout(10 seconds)
  override val logger: LoggingAdapter = Logging(system, getClass)

  val route: Route =
    path("airports") {
      get {
        parameter("q") { (query) =>
          logger.info(s"Querying airports with:$query")
          countryService.checkCountry(query) match {
            case Some(country) =>
              logger.debug(s"Checking airports for country:${country.name}")
              airportService.getAirportsWithRunways(country) match {
                case Some(airports) => respondWithHeaders(RawHeader("Content-Type", "application/json")) {
                  complete(OK, Json.obj("airports" -> airports.map(_.json)).toString())
                }
                case _ => complete(NotFound)
              }
            case _ =>
              logger.debug(s"No country information found for:${query}")
              complete(NotFound)
          }
        }
      }
    }
}