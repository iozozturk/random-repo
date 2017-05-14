package common

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import services.{AirportService, ElasticService, IndexService}

trait IndexSystem extends AirportSystem {

  def deleteIndex = if (checkIndexExists)
    client.admin().indices().delete(new DeleteIndexRequest(indexName)).get()

  def checkIndexExists = client
    .admin()
    .indices()
    .exists(new IndicesExistsRequest(indexName))
    .get()
    .isExists

  def checkAndCreateIndex = new ElasticService().checkAndCreateIndex()

  val indexService = new IndexService(new AirportService)
}
