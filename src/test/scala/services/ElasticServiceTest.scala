package services

import akka.event.{Logging, LoggingAdapter}
import common.IndexSystem
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}

class ElasticServiceTest extends WordSpec with Matchers with BeforeAndAfterEach with IndexSystem {

  override def afterEach(): Unit = deleteIndex

  val esService = new ElasticService

  "ESService" should {

    "checkAndCreateIndex" in {
      esService.checkAndCreateIndex()
      esService.client.admin().indices().exists(new IndicesExistsRequest(indexName)).get().isExists shouldEqual true

      val mappingsResponse = esService.client.admin().indices().getMappings(new GetMappingsRequest()).get()
      mappingsResponse.getMappings.get(indexName).containsKey("airports") shouldEqual true
      mappingsResponse.getMappings.get(indexName).containsKey("runways") shouldEqual true
      mappingsResponse.getMappings.get(indexName).containsKey("countries") shouldEqual true
    }

  }

  override val logger: LoggingAdapter = Logging(system, getClass)
}
