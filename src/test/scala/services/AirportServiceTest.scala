package services

import org.scalatest.{Matchers, WordSpec}

class AirportServiceTest extends WordSpec  with Matchers{


  "AirportServiceTest" should {

    "parse airport data toJson" in {
      val airportsToJson = AirportService.parseAirportsToJson()
      (airportsToJson.head \ "id").as[String] shouldEqual "1"
      (airportsToJson.head \ "ident").as[String] shouldEqual "0A"
      (airportsToJson.head \ "type").as[String] shouldEqual "heliport"
    }

    "parse country data toJson" in {
      val airportsToJson = AirportService.parseCountriesToJson()
      (airportsToJson.head \ "id").as[String] shouldEqual "1"
      (airportsToJson.head \ "code").as[String] shouldEqual "TR"
      (airportsToJson.head \ "name").as[String] shouldEqual "Turkey"
    }

    "parse runway data toJson" in {
      val airportsToJson = AirportService.parseRunwaysToJson()
      (airportsToJson.head \ "id").as[String] shouldEqual "1"
      (airportsToJson.head \ "airport_ref").as[String] shouldEqual ""
      (airportsToJson.head \ "airport_ident").as[String] shouldEqual "three"
      (airportsToJson.head \ "length_ft").as[String] shouldEqual "4.0"
      (airportsToJson.head \ "width_ft").as[String] shouldEqual "-5.1"
    }

  }
}
