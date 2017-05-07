package services

import akka.event.{Logging, LoggingAdapter}
import com.google.inject.Singleton
import com.typesafe.config.{Config, ConfigFactory}
import common.AirportSystem
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import org.elasticsearch.common.xcontent.XContentType

import scala.io.Source

@Singleton
class ElasticService extends AirportSystem {
  override val logger: LoggingAdapter = Logging(system, getClass)

  private val config: Config = ConfigFactory.load()

  def checkAndCreateIndex(): Unit = {
    if (indexExists)
      logger.info("Index already exists")
    else {
      logger.info("Creating search index")
      val createIndexResponse = createIndex
      if (createIndexResponse.isAcknowledged)
        logger.info("Search index created successfully")
      else
        logger.error("Creating search index failed!")
    }
  }

  private def indexExists = client.admin().indices().exists(new IndicesExistsRequest(indexName)).get().isExists

  private def createIndex = {
    val airportMapping = Source.fromResource("mappings/airports.json").getLines().map(_.trim).mkString
    val runwayMapping = Source.fromResource("mappings/runways.json").getLines().map(_.trim).mkString
    val countryMapping = Source.fromResource("mappings/countries.json").getLines().map(_.trim).mkString

    client.admin().indices()
      .prepareCreate(indexName)
      .addMapping("airports", airportMapping.toString, XContentType.JSON)
      .addMapping("runways", runwayMapping.toString, XContentType.JSON)
      .addMapping("countries", countryMapping.toString, XContentType.JSON)
      .get()
  }

}
