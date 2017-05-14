package services

import akka.event.{Logging, LoggingAdapter}
import com.google.inject.Singleton
import common.AirportSystem
import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.index.query.QueryBuilders
import play.api.libs.json.{JsObject, Json}

@Singleton
class CountryService extends AirportSystem {


  def checkCountry(countryQuery: String): Option[Country] = {
    val esQuery = searchClient.setTypes("countries")

    val searchResponse = if (countryQuery.length == 2)
     esQuery.setQuery(QueryBuilders.matchQuery("code", countryQuery)).get()
    else
     esQuery.setQuery(QueryBuilders.matchQuery("name", countryQuery).fuzziness(Fuzziness.AUTO)).get()

    if (searchResponse.getHits.hits().length > 0) {
      val country = Country(Json.parse(searchResponse.getHits.getAt(0).getSourceAsString).as[JsObject]) //  todo improve for multi match countries
      Some(country)
    } else None
  }

  override val logger: LoggingAdapter = Logging(system, getClass)
}

case class Country(json: JsObject) {
  val countryCode = (json \ "code").as[String]
  val name = (json \ "name").as[String]
}