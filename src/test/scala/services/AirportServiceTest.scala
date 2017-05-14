package services

import akka.event.{Logging, LoggingAdapter}
import common.IndexSystem
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import play.api.libs.json.{JsObject, Json}

class AirportServiceTest extends WordSpec with Matchers with IndexSystem with BeforeAndAfterEach {

  override def afterEach(): Unit = deleteIndex
  override def beforeEach(): Unit = checkAndCreateIndex

  val airportService = new AirportService


  val countryTurkey =
    Country(Json.parse(
      """
        |{
        |   "id":"1",
        |   "code":"TR",
        |   "name":"Turkey",
        |   "continent":"EU"
        |}
      """.stripMargin
    ).as[JsObject])

  val airportTurkey =
    Airport(Json.parse(
      """
        |{
        |   "id":"4528",
        |   "ident":"LTBA",
        |   "name":"Atatürk International Airport",
        |   "iso_country":"TR"
        |}
      """.stripMargin
    ).as[JsObject])

  val runway =
    Runway(Json.parse(
      """
        |{
        |   "id":"239260",
        |   "airport_ref":"4528",
        |   "airport_ident":"LTBA"
        |}
      """.stripMargin
    ).as[JsObject])

  "AirportService" should {

    "parse airport data toJson" in {
      val airportsToJson = airportService.parseRecordsToJson("airports")
      (airportsToJson.head \ "id").as[String] shouldEqual "1"
      (airportsToJson.head \ "ident").as[String] shouldEqual "0A"
      (airportsToJson.head \ "type").as[String] shouldEqual "heliport"
    }

    "parse country data toJson" in {
      val airportsToJson = airportService.parseRecordsToJson("countries")
      (airportsToJson.head \ "id").as[String] shouldEqual "1"
      (airportsToJson.head \ "code").as[String] shouldEqual "TR"
      (airportsToJson.head \ "name").as[String] shouldEqual "Turkey"
    }

    "parse runway data toJson" in {
      val airportsToJson = airportService.parseRecordsToJson("runways")
      (airportsToJson.head \ "id").as[String] shouldEqual "1"
      (airportsToJson.head \ "airport_ref").as[String] shouldEqual ""
      (airportsToJson.head \ "airport_ident").as[String] shouldEqual "three"
      (airportsToJson.head \ "length_ft").as[String] shouldEqual "4.0"
      (airportsToJson.head \ "width_ft").as[String] shouldEqual "-5.1"
    }

    "find airports and runways belonging to a country" in {
      indexService.indexAllData

      val airportsWithRunways = airportService.getAirportsWithRunways(countryTurkey)

      airportsWithRunways.get.head.ident shouldEqual airportTurkey.ident
      airportsWithRunways.get.head.runways.head.airportIdent shouldEqual runway.airportIdent
    }

  }

  override val logger: LoggingAdapter = Logging(system, getClass)
}
