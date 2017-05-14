package services

import akka.event.{Logging, LoggingAdapter}
import common.IndexSystem
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import play.api.libs.json.{JsObject, Json}

class CountryServiceTest extends WordSpec with Matchers with MockitoSugar with IndexSystem with BeforeAndAfterEach {
  override def afterEach(): Unit = deleteIndex

  override def beforeEach(): Unit = {
    checkAndCreateIndex
    indexService.indexAllData
  }

  val countryService = new CountryService()

  val countryTurkey =
    Country(Json.parse(
      """
        |{
        |"id":"1",
        |"code":"TR",
        |"name":"Turkey",
        |"continent":"EU",
        |"wikipedia_link":"http://en.wikipedia.org/wiki/Turkey",
        |"keywords":"turkey"
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


  "CountryServiceTest" should {

    "checkCountry with country code if exists" in {
      countryService.checkCountry("TR") shouldEqual Some(countryTurkey)
    }

    "checkCountry with country name if exists" in {
      countryService.checkCountry("Turkey") shouldEqual Some(countryTurkey)
    }

    "fuzzy match country name if exists" in {
      countryService.checkCountry("Turkiye") shouldEqual Some(countryTurkey)
    }

    "list top n countries having max number of airports" in {
      countryService.getHavingMaxAirports(1) shouldEqual Map("TR" -> 2)
    }

    "list top n countries having min number of airports" in {
      countryService.getHavingMinAirports(1) shouldEqual Map("US" -> 1)
    }

  }
  override val logger: LoggingAdapter = Logging(system, getClass)
}
