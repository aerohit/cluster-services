import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._
import com.typesafe.config.ConfigFactory

import scala.util.Failure
import scala.util.Success
import scala.concurrent.Future
import akka.http.scaladsl.model._

object FrontendServer extends App {

  import JsonFormats._

  implicit val system           = ActorSystem("frontend-system")
  implicit val materializer     = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val hostName = java.net.InetAddress.getLocalHost.getHostName

  val config             = ConfigFactory.load()
  val backendHost        = config.getString("backend.host")
  val backendHostnameUri = s"$backendHost/hostname"

  val consulHost = config.getString("consul.host")

  val route =
    path("serverinfo") {
      get {
        val responseFuture = constructResponse()
        onComplete(responseFuture) {
          case Success(response) => complete(response)
          case Failure(ex)       => complete(s"An error occurred: ${ex.getMessage}")
        }
      }
    } ~ path("consulservices") {
      get {
        onComplete(getConsulServices()) {
          case Success(response) => complete(response)
          case Failure(ex)       => complete(s"An error occurred: ${ex.getMessage}")
        }
      }
    } ~ path("consulnodes") {
      get {
        onComplete(getConsulServices()) {
          case Success(response) => complete(response)
          case Failure(ex)       => complete(s"An error occurred: ${ex.getMessage}")
        }
      }
    }

  // if exposed "localhost" only requests from docker container would be accepted
  Http().bindAndHandle(route, "0.0.0.0", 9100)

  def constructResponse(): Future[ServerResponse] = {
    getBackendHostname().map(info => ServerResponse(frontendHost = hostName, backendHost = info.hostname))
  }

  def getBackendHostname(): Future[ServerInfo] = {
    import akka.http.scaladsl.unmarshalling.Unmarshal
    val responseFuture = Http().singleRequest(HttpRequest(uri = backendHostnameUri))
    responseFuture.flatMap(resp => Unmarshal(resp.entity).to[ServerInfo])
  }

  def getConsulServices(): Future[String] = {
    import akka.http.scaladsl.unmarshalling.Unmarshal
    val responseFuture = Http().singleRequest(HttpRequest(uri = s"$consulHost/v1/agent/services"))
    responseFuture.flatMap(resp => Unmarshal(resp.entity).to[String])
  }

  def getConsulNodes(): Future[String] = {
    import akka.http.scaladsl.unmarshalling.Unmarshal
    val responseFuture = Http().singleRequest(HttpRequest(uri = s"$consulHost/v1/catalog/nodes"))
    responseFuture.flatMap(resp => Unmarshal(resp.entity).to[String])
  }
}

// TODO: this common class should be moved to a common project
case class ServerInfo(hostname: String)
case class ServerResponse(frontendHost: String, backendHost: String)

object JsonFormats extends SprayJsonSupport with DefaultJsonProtocol {
  implicit lazy val serverInfoFormat: RootJsonFormat[ServerInfo]         = jsonFormat1(ServerInfo)
  implicit lazy val serverResponseFormat: RootJsonFormat[ServerResponse] = jsonFormat2(ServerResponse)
}
