package services

import com.github.tototoshi.csv.CSVReader
import play.api.libs.json.{JsValue, Json}

import scala.io.Source

object AirportService {

  def parseAirportsToJson: List[JsValue] = parseFileToJson("airports")

  def parseCountriesToJson: List[JsValue] = parseFileToJson("countries")

  def parseRunwaysToJson: List[JsValue] = parseFileToJson("runways")

  private def parseFileToJson(resourceName: String) = {
    val reader = CSVReader.open(Source.fromResource(s"$resourceName.csv"))
    val values = reader.allWithHeaders()
    reader.close()
    values.map(Json.toJson(_))
  }

}
