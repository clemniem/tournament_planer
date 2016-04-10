package RestConnection

import Import.FileImport.FileImporter.BrokenLines
import TeamBuilding.Evaluation.TeamEvaluator.FinalTeamsLinesWithValue
import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import master.UeberActor
import master.UeberActor.ResultTeamsAndEval
import spray.json.{DefaultJsonProtocol, _}

import scala.concurrent.duration._

case class TeamRequest(players: List[String], teamCount: Int, weightVector: Vector[Int])

trait Protocols extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val teamRequest = jsonFormat3(TeamRequest.apply)
  implicit val brokenLine = jsonFormat1(BrokenLines.apply)
  implicit val resultData = jsonFormat2(ResultTeamsAndEval.apply)
}

trait Service extends Protocols{
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  import scala.concurrent.ExecutionContext.Implicits.global

  def config: Config
  val logger: LoggingAdapter

  val getTeamsRoute =
    path("getTeams") {
      (post & entity(as[TeamRequest])) { teamRequest =>
        implicit val timeout = Timeout(25.seconds)
        val ueberActor = system.actorOf(UeberActor.props)
        val futureAnswer = ueberActor ? teamRequest
        import akka.http.scaladsl.model.StatusCodes._
        complete {
          futureAnswer.map[ToResponseMarshallable] {
            case m: ResultTeamsAndEval => OK -> m.toJson
            case brokenLines: BrokenLines => BadRequest -> brokenLines.toJson
          }
        }
      }
    }
}


object AkkaHttpMicroservice extends App with Service {
  override implicit val system = ActorSystem("hat-planer")
  override implicit val materializer = ActorMaterializer()
  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)

  Http().bindAndHandle(getTeamsRoute, "0.0.0.0", 4321)
}