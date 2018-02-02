import com.typesafe.config.ConfigFactory

import scalaj.http._

object MyApp extends App {
  val config                         = ConfigFactory.load()
  val consulHost                     = config.getString("consul.host")
  val consulPort                     = config.getString("consul.port")
  val consulEndpoint                 = s"$consulHost:$consulPort"
  val response: HttpResponse[String] = Http(s"$consulEndpoint/v1/agent/services").asString
  println(response.body)
}
