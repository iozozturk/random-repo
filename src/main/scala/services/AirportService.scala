package services

import akka.event.{Logging, LoggingAdapter}
import com.github.tototoshi.csv.CSVReader
import com.google.inject.Singleton
import common.AirportSystem
import org.elasticsearch.index.query.QueryBuilders
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.io.Source

@Singleton
class AirportService extends AirportSystem {

  def parseRecordsToJson(recordType: String): List[JsValue] = {
    val reader = CSVReader.open(Source.fromResource(s"$recordType.csv"))
    val values = reader.allWithHeaders()
    reader.close()
    values.map(Json.toJson(_))
  }

  def getAirportsWithRunways(country: Country): Option[Seq[Airport]] = {
    val searchResponse = client.prepareSearch(indexName).setSize(maxResultSize).setTypes("airports")
      .setQuery(QueryBuilders.termQuery("iso_country", country.countryCode)).get()

    if (searchResponse.getHits.totalHits() > 0) {
      val airports = searchResponse.getHits.getHits.map { searchHit =>
        val airport = Airport(Json.parse(searchHit.getSourceAsString).as[JsObject])
        val response = client.prepareSearch(indexName).setSize(maxResultSize).setTypes("runways").setQuery(QueryBuilders.termQuery("airport_ident", airport.ident)).get()
        val runways = response.getHits.getHits.map(rw => Runway(Json.parse(rw.getSourceAsString).as[JsObject])).toSeq
        airport.withRunways(runways)
      }
      Some(airports.toSeq)
    }
    else
      None
  }

  override val logger: LoggingAdapter = Logging(system, getClass)
}

case class Airport(json: JsObject) {
  lazy val runways: Seq[Runway] = (json \ "runways").as[Seq[JsObject]].map(Runway)

  val ident = (json \ "ident").as[String]

  def withRunways(runways: Seq[Runway]) = runways.isEmpty match {
    case false => Airport(json ++ Json.obj("runways" -> runways.map(_.json)))
    case true => Airport(json)
  }
}
