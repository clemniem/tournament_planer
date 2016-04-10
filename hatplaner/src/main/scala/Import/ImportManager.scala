package Import

import Import.FileImport.FileImporter.{BrokenLines, PlayerList}
import Import.FileImport.FileImporter
import Import.ImportOptimizer.ImportOptimizer.FinalPlayers
import RestConnection.TeamRequest
import akka.actor.{ActorRef, Props, Actor}

/**
  * Created by yannick on 13.02.16.
  */
object ImportManager {
  val name = "import-manger"
  def props(master: ActorRef) = Props(new ImportManager(master))
}
case class PlayerListWithWeightVector(playerList: PlayerList, weightVector: Vector[Int])

class ImportManager(master: ActorRef) extends Actor{
  val importer = context.actorOf(FileImporter.props(self), FileImporter.name)
  val importOptimizer = context.actorOf(ImportOptimizer.ImportOptimizer.props(self), ImportOptimizer.ImportOptimizer.name)
  var weightVector = Vector.empty[Int]
  def receive = importFile

  def importFile: Receive = {
    case p @ TeamRequest(_,_,w) =>
      importer ! p
      weightVector = w
    case br @ BrokenLines(_) => master ! br
    case players @ PlayerList(_) => importOptimizer ! PlayerListWithWeightVector(players, weightVector)
      context become optimizeFile
  }

  def optimizeFile: Receive = {
    case f @ FinalPlayers(players, meanVals) => master ! f
  }
}