package services

import akka.event.{Logging, LoggingAdapter}
import common.IndexSystem
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import play.api.libs.json.{JsObject, Json}

class CountryServiceTest extends WordSpec with Matchers with MockitoSugar with IndexSystem with BeforeAndAfterEach {
  override def afterEach(): Unit = deleteIndex

  override def beforeEach(): Unit = checkAndCreateIndex

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


  "CountryServiceTest" should {

    "checkCountry with country code if exists" in {
      indexService.indexAllData
      countryService.checkCountry("TR") shouldEqual Some(countryTurkey)
    }

    "checkCountry with country name if exists" in {
      indexService.indexAllData
      countryService.checkCountry("Turkey") shouldEqual Some(countryTurkey)
    }

    "fuzzy match country name if exists" in {
      indexService.indexAllData
      countryService.checkCountry("Turkiye") shouldEqual Some(countryTurkey)
    }

  }
  override val logger: LoggingAdapter = Logging(system, getClass)
}
