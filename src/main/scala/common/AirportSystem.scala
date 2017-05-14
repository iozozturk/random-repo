package common

import java.net.InetAddress

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.stream.ActorMaterializer
import com.typesafe.config.{Config, ConfigFactory}
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.InetSocketTransportAddress
import org.elasticsearch.transport.client.PreBuiltTransportClient

import scala.concurrent.ExecutionContextExecutor

trait AirportSystem {
  implicit val system: ActorSystem = ActorSystem("airport-system")
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  private val config: Config = ConfigFactory.load()
  private val host: String = config.getString("es.host")
  val indexName: String = config.getString("es.index.name")

  val maxResultValue = 10000
  val client = new PreBuiltTransportClient(Settings.EMPTY)
    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), 9300))


  val logger: LoggingAdapter

}