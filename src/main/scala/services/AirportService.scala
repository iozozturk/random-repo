package services

import com.github.tototoshi.csv.CSVReader
import play.api.libs.json.Json

import scala.io.Source


object AirportService {

  def parseAirportsToJson() = {
    val reader = CSVReader.open(Source.fromResource("airports.csv"))
    val values = reader.allWithHeaders()
    reader.close()
    values.map(Json.toJson(_))
  }

}
