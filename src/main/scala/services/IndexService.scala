package services

import javax.inject.{Inject, Singleton}

import akka.event.{Logging, LoggingAdapter}
import common.AirportSystem
import org.elasticsearch.common.xcontent.XContentType
import play.api.libs.json.JsValue

@Singleton
class IndexService @Inject()(airportService: AirportService) extends AirportSystem {

  val dataTypes = Array("airports", "runways", "countries")

  def indexAllData = {
    dataTypes.foreach { dataType =>
      airportService.parseRecordsToJson(dataType).grouped(10).foreach(batch => indexBatch(batch, dataType))
    }
    client.admin().indices().prepareRefresh(indexName).get()
  }

  private def indexBatch(batch: List[JsValue], batchType: String) = {
    val bulkRequestBuilder = client.prepareBulk()
    batch.foreach { record =>
      bulkRequestBuilder.add(client
        .prepareIndex(indexName, batchType, (record \ "id").as[String])
        .setSource(record.toString(), XContentType.JSON))
    }
    val bulkResponse = bulkRequestBuilder.get()
    if (bulkResponse.hasFailures) {
      logger.warning(bulkResponse.buildFailureMessage())
    }
  }

  override val logger: LoggingAdapter = Logging(system, getClass)
}
