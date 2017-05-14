package services

import akka.event.{Logging, LoggingAdapter}
import common.IndexSystem
import org.elasticsearch.index.query.QueryBuilders
import org.mockito.Mockito.when
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import play.api.libs.json.Json


class IndexServiceTest extends WordSpec with Matchers with MockitoSugar with IndexSystem with BeforeAndAfterEach {
  override def afterEach(): Unit = deleteIndex

  val airportService: AirportService = mock[AirportService]
  override val indexService = new IndexService(airportService)
  checkAndCreateIndex

  "IndexService" should {

    "indexAllData" in {
      when(airportService.parseRecordsToJson("airports")) thenReturn List(Json.obj("id" -> "1", "ident" -> "LTBA", "name" -> "Halifax"))
      when(airportService.parseRecordsToJson("runways")) thenReturn List(Json.obj("id" -> "1", "airport_ident" -> "LTBA", "name" -> "Longest"))
      when(airportService.parseRecordsToJson("countries")) thenReturn List(Json.obj("id" -> "1", "name" -> "US"))
      indexService.indexAllData
      val searchHits = client.prepareSearch(indexName).setTypes("airports").setQuery(QueryBuilders.termQuery("name", "Halifax")).get().getHits.getHits
      searchHits.length shouldEqual 1L
    }

  }
  override val logger: LoggingAdapter = Logging(system, getClass)
}
