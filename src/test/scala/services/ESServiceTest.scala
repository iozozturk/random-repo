package services

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest
import org.scalatest.{Matchers, WordSpec}

class ESServiceTest extends WordSpec with Matchers {

  val esService = new ElasticService
  val indexName = esService.indexName

  "ESService" should {

    "checkAndCreateIndex" in {
      if (checkIndexExists)
        esService.client.admin().indices().delete(new DeleteIndexRequest(indexName)).get()

      esService.checkAndCreateIndex()
      esService.client.admin().indices().exists(new IndicesExistsRequest(indexName)).get().isExists shouldEqual true

      val mappingsResponse = esService.client.admin().indices().getMappings(new GetMappingsRequest()).get()
      mappingsResponse.getMappings.get(indexName).containsKey("airports") shouldEqual true
      mappingsResponse.getMappings.get(indexName).containsKey("runways") shouldEqual true
      mappingsResponse.getMappings.get(indexName).containsKey("countries") shouldEqual true
    }

  }

  def checkIndexExists = esService.client
    .admin()
    .indices()
    .exists(new IndicesExistsRequest(indexName))
    .get()
    .isExists
}
