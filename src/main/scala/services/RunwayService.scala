package services

import akka.event.{Logging, LoggingAdapter}
import com.google.inject.Singleton
import common.AirportSystem
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.aggregations.bucket.filter.Filter
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import play.api.libs.json.JsObject

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConverters.asJavaCollectionConverter

@Singleton
class RunwayService extends AirportSystem {

  def getMostCommonIdents(numberOfIdents: Int) = {
    val countryAggregation = AggregationBuilders.terms("by_ident").field("le_ident").size(numberOfIdents).order(Terms.Order.term(true))
    val searchResponse = client.prepareSearch(indexName).setSize(0).addAggregation(countryAggregation).get()
    val buckets = searchResponse.getAggregations.get[Terms]("by_ident").getBuckets.toSeq

    buckets.map { bucket =>
      val ident = bucket.getKeyAsString.replace("\"", "")
      val numberOfRunwayIdents = bucket.getDocCount
      (ident, numberOfRunwayIdents)
    }.toMap
  }


  def getRunwaySurfacesByCountry = {
    val airportIdentsByCountry = getAirportIdentsByCountry
    val query = client.prepareSearch(indexName).setSize(0)

    airportIdentsByCountry.foreach { countryIdents =>
      query.addAggregation(AggregationBuilders.filter(s"by_${countryIdents._1}", QueryBuilders.termsQuery("airport_ident", countryIdents._2.asJavaCollection))
        .subAggregation(AggregationBuilders.terms("by_surface").field("surface").size(maxResultSize)))
    }
    val queryResponse = query.get()

    val surfacesByCountry = airportIdentsByCountry.map { countryIdents =>
      val countrySurfaceBuckets = queryResponse.getAggregations.get[Filter](s"by_${countryIdents._1}").getAggregations.get[Terms]("by_surface").getBuckets
      val countrySurfaces = countrySurfaceBuckets.map { surfaceBucket =>
        surfaceBucket.getKeyAsString
      }.toList
      (countryIdents._1, countrySurfaces)
    }
    surfacesByCountry
  }

  private def getAirportIdentsByCountry = {
    val countryAggregation = AggregationBuilders.terms("by_country").field("iso_country").size(maxResultSize)
      .subAggregation(AggregationBuilders.terms("by_ident").field("ident").size(maxResultSize))
    val searchResponse = client.prepareSearch(indexName).setSize(0).addAggregation(countryAggregation).get()
    val countryBuckets = searchResponse.getAggregations.get[Terms]("by_country").getBuckets.toSeq

    countryBuckets.map { bucket =>
      val countryCode = bucket.getKeyAsString
      val idents = bucket.getAggregations.get[Terms]("by_ident").getBuckets.toSeq
      (countryCode, idents.map(_.getKeyAsString))
    }.toMap
  }

  override val logger: LoggingAdapter = Logging(system, getClass)
}


case class Runway(json: JsObject) {
  val airportIdent = (json \ "airport_ident").as[String]
}
