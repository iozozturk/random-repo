package services

import org.scalatest.{Matchers, WordSpec}

class AirportServiceTest extends WordSpec  with Matchers{


  "AirportServiceTest" should {

    "parse Airports data ToJson" in {
      val airportsToJson = AirportService.parseAirportsToJson()
      (airportsToJson.head \ "id").as[String] shouldEqual "1"
      (airportsToJson.head \ "airport_ref").as[String] shouldEqual ""
      (airportsToJson.head \ "airport_ident").as[String] shouldEqual "three"
      (airportsToJson.head \ "length_ft").as[String] shouldEqual "4.0"
      (airportsToJson.head \ "width_ft").as[String] shouldEqual "-5.1"
    }

  }
}
