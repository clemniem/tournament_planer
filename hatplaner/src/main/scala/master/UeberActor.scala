package master

import Import.FileImport.FileImporter.{BrokenLines, ResponseResult}
import Import.Types._
import Import.{FinalPlayer, ImportManager}
import Import.ImportOptimizer.ImportOptimizer.FinalPlayers
import RestConnection.TeamRequest
import TeamBuilding.Evaluation.TeamEvaluator.FinalTeamsLinesWithValue
import TeamBuilding.TeamBuildingMaster
import akka.actor.{Actor, ActorRef, PoisonPill, Props}
import master.UeberActor._

import scala.collection.immutable.TreeMap

/**
  * Created by yannick on 16.02.16.
  */
object UeberActor {
  val name = "ueber-actor"

  def props = Props(new UeberActor)
  case class ResultPaths(teamsPath: String = "", plotPath: String = "") extends ResponseResult
  case class ResultTeamsAndEval(teams: List[String], eval: Seq[(Int,Int)])
  case class BestTeamComboAndEval(finalTeamsWithValue: FinalTeamsLinesWithValue, eval: TreeMap[Int,Int])
  case class FinalPlayersWithTeamNumber(players: List[FinalPlayer], meanVals: ValueVector, teamCount: Int)
  case class WeightVector(weightVector: ValueVector)
}

class UeberActor extends Actor {
  val importManager = context.actorOf(ImportManager.props(self), ImportManager.name)
  val teamBuldingMaster = context.actorOf(TeamBuildingMaster.props(self), TeamBuildingMaster.name)
  var realSender: ActorRef = null
  val startTime = System.currentTimeMillis()
  var resultPaths = ResultPaths()
  var numberOfTeams = 0

  def receive: Receive = {
    case t @ TeamRequest(_,count, weightVector) => importManager ! t
      numberOfTeams = count
      realSender = sender
      teamBuldingMaster ! WeightVector(weightVector)
      println("Sent to Importmanager")
    case FinalPlayers(players,meanVals) =>
      teamBuldingMaster ! FinalPlayersWithTeamNumber(players, meanVals, numberOfTeams)
      println("Sent to TeamBuildingMaster")
    case ft @ BestTeamComboAndEval(finalTeams,eval) =>
      realSender ! ResultTeamsAndEval(finalTeams.teams.toLines, eval.toSeq)
      self ! PoisonPill
      println("Time after calculations: " + (System.currentTimeMillis() - startTime) / 1000 + " sec.")

    case br @ BrokenLines(brokenList) =>
      realSender ! br
      self ! PoisonPill
  }

  def checkIfResult = {
    if(resultPaths.plotPath != "" && resultPaths.teamsPath != ""){
      realSender ! resultPaths
      println("Finished!")
      self ! PoisonPill
    }
  }
}