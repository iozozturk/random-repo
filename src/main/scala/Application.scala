import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import com.google.inject.Guice
import common.AirportSystem
import httpservices.AirportHttpService
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import net.codingwell.scalaguice.ScalaModule
import services.{ElasticService, IndexService}

import scala.concurrent.Await
import scala.concurrent.duration.DurationLong

object Application extends AirportSystem {

  def main(args: Array[String]): Unit = {

    val injector = Guice.createInjector()
    val airportHttpService = injector.instance[AirportHttpService]
    val esService = injector.instance[ElasticService]
    val indexService = injector.instance[IndexService]
    val route = airportHttpService.route

    esService.checkAndCreateIndex()
    indexService.indexAllData

    val binding = Await.result(Http().bindAndHandle(route, "0.0.0.0", 9000), 3.seconds)

    logger.info(s"Started server at ${binding.localAddress}")
  }

  override val logger: LoggingAdapter = Logging(system, getClass)
}

object MainModule extends ScalaModule {

  override def configure(): Unit = {
  }

}
