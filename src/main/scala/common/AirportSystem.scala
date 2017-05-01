package common

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContextExecutor

trait AirportSystem {
  implicit val system: ActorSystem = ActorSystem("airport-system")
  implicit val executor: ExecutionContextExecutor = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val logger: LoggingAdapter
}