package services

import com.github.tototoshi.csv.CSVReader
import com.google.inject.Singleton
import play.api.libs.json.{JsValue, Json}

import scala.io.Source

@Singleton
class AirportService {

  def parseRecordsToJson(recordType: String): List[JsValue] = {
    val reader = CSVReader.open(Source.fromResource(s"$recordType.csv"))
    val values = reader.allWithHeaders()
    reader.close()
    values.map(Json.toJson(_))
  }

}
