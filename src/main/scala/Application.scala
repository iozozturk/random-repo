import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import com.google.inject.Guice
import common.AirportSystem

import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector
import net.codingwell.scalaguice.ScalaModule
import httpservices.AirportHttpService

import scala.concurrent.Await
import scala.concurrent.duration.DurationLong

object Application extends AirportSystem {

  def main(args: Array[String]): Unit = {

    val injector = Guice.createInjector()
    val movieService = injector.instance[AirportHttpService]
    val route = movieService.route

    val binding = Await.result(Http().bindAndHandle(route, "0.0.0.0", 9000), 3.seconds)

    println(s"Started server at ${binding.localAddress}")
  }

  override val logger: LoggingAdapter = Logging(system, getClass)
}

object MainModule extends ScalaModule {

  override def configure(): Unit = {
  }

}
