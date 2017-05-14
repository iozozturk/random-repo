package services

import akka.event.{Logging, LoggingAdapter}
import com.google.inject.Singleton
import common.AirportSystem
import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.index.query.QueryBuilders
import play.api.libs.json.{JsObject, Json}

@Singleton
class RunwayService extends AirportSystem {

  override val logger: LoggingAdapter = Logging(system, getClass)
}


case class Runway(json: JsObject) {
  val airportIdent = (json \ "airport_ident").as[String]
}
