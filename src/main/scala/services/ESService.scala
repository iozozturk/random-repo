package services

import java.net.InetAddress

import akka.event.{Logging, LoggingAdapter}
import com.google.inject.Singleton
import com.typesafe.config.{Config, ConfigFactory}
import common.AirportSystem
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.transport.client.PreBuiltTransportClient

import scala.io.Source

@Singleton
class ESService extends AirportSystem {
  override val logger: LoggingAdapter = Logging(system, getClass)

  private val config: Config = ConfigFactory.load()

  private val host: String = config.getString("es.host")
  val indexName: String = config.getString("es.index.name")

  val client = new PreBuiltTransportClient(Settings.EMPTY)
    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), 9300))

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
