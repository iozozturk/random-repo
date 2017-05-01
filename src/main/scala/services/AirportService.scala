package services

import com.github.tototoshi.csv.CSVReader
import play.api.libs.json.Json

import scala.io.Source

object AirportService {

  def parseAirportsToJson() = parseFileToJson("airports")

  def parseCountriesToJson() = parseFileToJson("countries")

  def parseRunwaysToJson() = parseFileToJson("runways")

  private def parseFileToJson(resourceName: String) = {
    val reader = CSVReader.open(Source.fromResource(s"$resourceName.csv"))
    val values = reader.allWithHeaders()
    reader.close()
    values.map(Json.toJson(_))
  }

}
