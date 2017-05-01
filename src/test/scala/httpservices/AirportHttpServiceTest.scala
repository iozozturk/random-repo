package httpservices

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{AsyncWordSpec, Matchers}

class AirportHttpServiceTest extends AsyncWordSpec
  with Matchers with ScalatestRouteTest with MockitoSugar {

  val airportHttpService = new AirportHttpService

  "Airport Service" should {

    "query airport by country " in {
      Get("/airports?q=any") ~> airportHttpService.route ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

  }
}
