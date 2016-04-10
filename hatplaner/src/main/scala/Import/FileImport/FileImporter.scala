package Import.FileImport

import java.io.FileNotFoundException

import Import.{Player, Routing}
import RestConnection.TeamRequest
import akka.actor.SupervisorStrategy.{Directive, Escalate}
import akka.actor._

/**
  * handles the file import and the line validation
  */
object FileImporter {
  val name = "importer"

  def props(sendTo: ActorRef) = Props(new FileImporter(sendTo))

  trait ResponseResult

  case class PlayerList(players: List[Player]) extends ResponseResult

  case class ImportFile(l: List[String])

  case class BrokenLines(broken: List[String]) extends ResponseResult

}

class FileImporter(sendTo: ActorRef) extends Actor with Routing with ImportSupervising {

  import FileImporter._
  import CsvLineImporter._

  var linesCount = 0
  var players = List.empty[Player]
  var brokenLines = List.empty[String]

  val router = createRouter(context, 4, CsvLineImporter.props)

  //checks if all lines have been imported or errors were found
  def checkIfReady = {
    if (players.length + brokenLines.length == linesCount)
      if (brokenLines.isEmpty) sendTo ! PlayerList(players)
      else sendTo ! BrokenLines(brokenLines)
  }

  def receive = loadFile

  //state that handles the file import
  def loadFile: Receive = {
    case t@TeamRequest(_,_,_) => t.players foreach (line => router.route(InputLine(line), self))
      linesCount = t.players.length
      context become validateFile
  }

  //state that handles the validation of the line
  def validateFile: Receive = {
    case p@Player(_,_,_,_,_,_) => players = p :: players
      checkIfReady
    case ErrorInLine(message) => brokenLines = message :: brokenLines
      checkIfReady
  }
}

trait ImportSupervising extends Actor {
  val decider: PartialFunction[Throwable, Directive] = {
    case _: FileNotFoundException => Escalate
  }

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy()(decider.orElse(SupervisorStrategy.defaultStrategy.decider))
}