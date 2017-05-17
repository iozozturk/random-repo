# Airport Service

Example micro-service application showcasing a simple http server parsing CSV to JSON then store at Elasticsearch and serve through Akka-Http

- Akka-Http for lightweight Http API
- Index data at Elasticsearch
- Guice for dependency injection
- Logging
- ScalaTest and Mockito at tests
- Unit tests, mocking layers at each level
- Integration tests at Elasticsearch integration points
- Reading separate configuration for test
- Play Json library for convenience
- Sbt-Revolver for quick reloads at development

## Example Usage

#### Run Service for development:
Please make sure you have Elasticsearch 5.X running at the host/port defined at application.conf

```
$ sbt 
> re-start
```

#### Query airports by Country Code or Name:

```
$ curl http://localhost:9000/airports?q=tr

$ curl http://localhost:9000/airports?q=turkey
```
#### Get a report on min/max number of airports by country

```
$ curl http://localhost:9000/reports
```
##### Response:
```
{
  "countriesWithMaxAirports": {
    "RU": 920,
    "VE": 592,
    "US": 21501,
    "AU": 1908,
    "AR": 713,
    "FR": 789,
    "CO": 700,
    "BR": 3839,
    "CA": 2454,
    "DE": 703
  },
  "countriesWithMinAirports": {
    "VN": 42,
    "WF": 2,
    "ZZ": 1,
    "ZW": 83,
    "WS": 4,
    "ZM": 76,
    "YT": 1,
    "YE": 25,
    "ZA": 445,
    "VU": 32
  },
  "mostCommonRunwayIdents": {
    "": 117,
    "01H": 1,
    "007": 1,
    "16R": 1,
    "01L": 2,
    "0": 1,
    "16L": 1,
    "01C": 1,
    "01R": 2,
    "01": 1144
  },
  "runwaysByCountry": {
    "TR": [
        "ASP",
        "UNK"
    ]
  }
}
```

#### Testing Services
```
$ sbt test
```