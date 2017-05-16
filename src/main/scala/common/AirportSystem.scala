package common

import java.net.InetAddress

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient

import scala.concurrent.ExecutionContextExecutor

trait AirportSystem {
  implicit val system: ActorSystem = ActorSystem("airport-system")
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val maxResultSize = 50000
  val client: TransportClient = Constants.client
  val indexName: String = Constants.indexName

  val logger: LoggingAdapter
}

object Constants {
  private val config: Config = ConfigFactory.load()
  private val host: String = config.getString("es.host")
  private val port: Int = config.getInt("es.port")
  val indexName: String = config.getString("es.index.name")

  val client = new PreBuiltTransportClient(Settings.EMPTY)
    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), port))
}