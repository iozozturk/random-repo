package httpservices

import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes.{NotFound, OK}
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, MediaTypes}
import akka.http.scaladsl.server.Directives.{complete, path, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.util.Timeout
import com.google.inject.{Inject, Singleton}
import common.AirportSystem
import play.api.libs.json.{JsObject, Json}
import services.{AirportService, CountryService, RunwayService}
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
                                   countryService: CountryService,
                                   runwayService: RunwayService) extends AirportSystem with Protocols {
  implicit val timeout = Timeout(10 seconds)
  override val logger: LoggingAdapter = Logging(system, getClass)
  val jsonContentType = MediaTypes.`application/json`.toContentType

  val route: Route =
    path("airports") {
      get {
        parameter("q") { (query) =>
          logger.info(s"Querying airports with:$query")
          countryService.checkCountry(query) match {
            case Some(country) =>
              logger.debug(s"Checking airports for country:${country.name}")
              airportService.getAirportsWithRunways(country) match {
                case Some(airports) =>
                  val airportData = Json.obj("airports" -> airports.map(_.json)).toString()
                  complete(HttpResponse(OK, entity = HttpEntity(jsonContentType, airportData)))
                case _ => complete(NotFound)
              }
            case _ =>
              logger.debug(s"No country information found for:$query")
              complete(NotFound)
          }
        }
      }
    } ~
      path("reports") {
        get {
          logger.info(s"Getting airport report")
          val countriesWithMaxAirports = countryService.getHavingMaxAirports(10)
          val countriesWithMinAirports = countryService.getHavingMinAirports(10)
          val mostCommonRunways = runwayService.getMostCommonIdents(10)
          complete(OK, Json.obj(
            "countriesWithMaxAirports" -> countriesWithMaxAirports,
            "countriesWithMinAirports" -> countriesWithMinAirports,
            "mostCommonRunwayIdents" -> mostCommonRunways
          ).toString()
          )
        }
      }

}