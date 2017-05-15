package httpservices

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{AsyncWordSpec, Matchers}
import play.api.libs.json.Json
import services._

class AirportHttpServiceTest extends AsyncWordSpec
  with Matchers with ScalatestRouteTest with MockitoSugar with Protocols {

  val airportService = mock[AirportService]
  val countryService = mock[CountryService]
  val runwayService = mock[RunwayService]
  val airportHttpService = new AirportHttpService(airportService, countryService, runwayService)

  val countryTR = Country(Json.obj("code" -> "tr", "name" -> "Turkey"))
  val airportWithRunway = Airport(Json.obj(
    "name" -> "aiport1",
    "ident" -> "LTBA",
    "runways" -> Seq(Json.obj("name" -> "runway1", "airport_ident" -> "LTBA"))
  ))

  "Airport Service" should {

    "query airport by existing country " in {
      val query = "any"

      when(countryService.checkCountry(query)) thenReturn Some(countryTR)
      when(airportService.getAirportsWithRunways(countryTR)) thenReturn Some(Seq(airportWithRunway))

      Get("/airports?q=" + query) ~> airportHttpService.route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual Json.obj("airports" -> Seq(airportWithRunway).map(_.json)).toString()
      }
    }

    "query airport by non-existing country " in {
      val query = "any"

      when(countryService.checkCountry(query)) thenReturn None

      Get("/airports?q=" + query) ~> airportHttpService.route ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "query airport by non-existing airports at existing country " in {
      val query = "any"

      when(countryService.checkCountry(query)) thenReturn Some(countryTR)
      when(airportService.getAirportsWithRunways(countryTR)) thenReturn None

      Get("/airports?q=" + query) ~> airportHttpService.route ~> check {
        status shouldEqual StatusCodes.NotFound
      }
    }

    "create reports about countries with min/max number of airports" in {
      val countriesWithMaxAirports = Map("TR" -> 1L)
      val countriesWithMinAirports = Map("US" -> 1L)
      val mostCommonRunwayIdents = Map("NL" -> 1L)
      when(countryService.getHavingMaxAirports(10)) thenReturn countriesWithMaxAirports
      when(countryService.getHavingMinAirports(10)) thenReturn countriesWithMinAirports
      when(runwayService.getMostCommonIdents(10)) thenReturn mostCommonRunwayIdents

      Get("/reports") ~> airportHttpService.route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual Json.obj(
          "countriesWithMaxAirports" -> countriesWithMaxAirports,
          "countriesWithMinAirports" -> countriesWithMinAirports,
          "mostCommonRunwayIdents" -> mostCommonRunwayIdents).toString()
      }
    }

  }
}
