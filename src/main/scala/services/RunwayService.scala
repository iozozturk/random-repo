package services

import akka.event.{Logging, LoggingAdapter}
import com.google.inject.Singleton
import common.AirportSystem
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import play.api.libs.json.JsObject

import scala.collection.JavaConversions.asScalaBuffer

@Singleton
class RunwayService extends AirportSystem {

  def getMostCommonIdents(numberOfIdents: Int) = {
    val countryAggregation = AggregationBuilders.terms("by_ident").field("le_ident").size(numberOfIdents).order(Terms.Order.term(true))
    val searchResponse = client.prepareSearch(indexName).setSize(0).addAggregation(countryAggregation).get()
    val buckets = searchResponse.getAggregations.get[Terms]("by_ident").getBuckets.toSeq

    buckets.map { bucket =>
      val ident = bucket.getKeyAsString
      val numberOfRunwayIdents = bucket.getDocCount
      (ident, numberOfRunwayIdents)
    }.toMap
  }


  def getRunwaySurfacesByCountry: JsObject = {
    ???
  }

  override val logger: LoggingAdapter = Logging(system, getClass)
}


case class Runway(json: JsObject) {
  val airportIdent = (json \ "airport_ident").as[String]
}
