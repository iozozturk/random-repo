package services

import akka.event.{Logging, LoggingAdapter}
import com.google.inject.Singleton
import common.AirportSystem
import org.elasticsearch.common.unit.Fuzziness
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import play.api.libs.json.{JsObject, Json}

import scala.collection.JavaConversions.asScalaBuffer

@Singleton
class CountryService extends AirportSystem {

  def getHavingMaxAirports(numberOfCountries: Int): Map[String, Long] = {
    val countryAggregation = AggregationBuilders.terms("airport_country").field("iso_country").size(numberOfCountries)
    val searchResponse = client.prepareSearch(indexName).setSize(0).addAggregation(countryAggregation).get()
    val buckets = searchResponse.getAggregations.get[Terms]("airport_country").getBuckets.toSeq

    buckets.map { bucket =>
      val countryCode = bucket.getKeyAsString
      val numberOfAirports = bucket.getDocCount
      (countryCode,numberOfAirports)
    }.toMap
  }

  def getHavingMinAirports(numberOfCountries: Int): Map[String, Long] = {
    val countryAggregation = AggregationBuilders.terms("airport_country").field("iso_country").size(numberOfCountries).order(Terms.Order.term(false))
    val searchResponse = client.prepareSearch(indexName).setSize(0).addAggregation(countryAggregation).get()
    val buckets = searchResponse.getAggregations.get[Terms]("airport_country").getBuckets.toSeq

    buckets.map { bucket =>
      val countryCode = bucket.getKeyAsString
      val numberOfAirports = bucket.getDocCount
      (countryCode,numberOfAirports)
    }.toMap
  }


  def checkCountry(countryQuery: String): Option[Country] = {
    val esQuery = client.prepareSearch(indexName).setSize(maxResultSize).setTypes("countries")

    val searchResponse = if (countryQuery.length == 2)
      esQuery.setQuery(QueryBuilders.matchQuery("code", countryQuery.toUpperCase)).get()
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

  def withAirports(airports: Seq[Airport]) = airports.isEmpty match {
    case false => Country(json ++ Json.obj("airports" -> airports.map(_.json)))
    case true => Country(json)
  }
}