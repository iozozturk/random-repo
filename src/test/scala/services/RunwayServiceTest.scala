package services

import akka.event.{Logging, LoggingAdapter}
import common.IndexSystem
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import play.api.libs.json.Json

class RunwayServiceTest extends WordSpec with Matchers with MockitoSugar with IndexSystem with BeforeAndAfterEach {
  override def afterEach(): Unit = deleteIndex

  override def beforeEach(): Unit = {
    checkAndCreateIndex
    indexService.indexAllData
  }

  val runwayService = new RunwayService()

  val runwaySurfacesByCountry = Json.parse(
    """
      |{
      |   "TR":["ASP"]
      |}
    """.stripMargin
  )


  "RunwayService" should  {
//     "list runway surfaces by country" in {
//      runwayService.getRunwaySurfacesByCountry shouldEqual runwaySurfacesByCountry
//    }

    "list most common runway idents" in {
      runwayService.getMostCommonIdents(1) shouldEqual Map("05" -> 1)
    }

  }
  override val logger: LoggingAdapter = Logging(system, getClass)
}
