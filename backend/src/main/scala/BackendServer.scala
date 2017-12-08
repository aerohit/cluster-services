import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

object BackendServer extends App {

  import JsonFormats._

  implicit val system           = ActorSystem("backend-system")
  implicit val materializer     = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val hostName   = java.net.InetAddress.getLocalHost.getHostName
  val serverInfo = ServerInfo(hostName)

  val route =
    path("hostname") {
      get {
        complete(serverInfo)
      }
    }

  // if exposed "localhost" only requests from docker container would be accepted
  Http().bindAndHandle(route, "0.0.0.0", 9000)
}

case class ServerInfo(hostname: String)

object JsonFormats extends SprayJsonSupport with DefaultJsonProtocol {
  implicit lazy val serverInfoFormat: RootJsonFormat[ServerInfo] = jsonFormat1(ServerInfo)
}
