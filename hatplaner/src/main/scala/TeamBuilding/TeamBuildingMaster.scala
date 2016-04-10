package TeamBuilding

import Import.ImportOptimizer.ImportOptimizer.{FinalPlayers}
import Import.Types._
import Import.{FinalPlayer, TeamsLines, Routing}
import TeamBuilding.Evaluation.{GenderCombinator, TeamEvaluator}
import TeamBuilding.Evaluation.TeamEvaluator.{FinalTeamsWithValue, FinalTeamsLinesWithValue}
import TeamBuilding.GenderSeparation.GenderSeparator
import GenderSeparator.SeparatedPlayers
import TeamBuilding.NaiveAlgorithm.NaiveTeamBuilder
import TeamBuilding.NaiveAlgorithm.NaiveTeamBuilder.LastMessage
import TeamBuilding.TeamBuildingMaster.PlotData
import akka.actor.{ActorRef, Props, Actor}
import master.UeberActor.{WeightVector, FinalPlayersWithTeamNumber, BestTeamComboAndEval}

import scala.collection.immutable.TreeMap

/**
  * Created by yannick on 16.02.16.
  */
object TeamBuildingMaster {
  val name = "team-master"
  def props(master: ActorRef) = Props(new TeamBuildingMaster(master))
  case class PlotData(evalMap: TreeMap[Int,Int])
}
case class SeparatedPlayersWithCount(malePlayers: List[FinalPlayer], femalePlayers: List[FinalPlayer], meanVals: ValueVector, teamCount:Int)

class TeamBuildingMaster(master: ActorRef) extends Actor with Routing {
  val genderSeparator = context.actorOf(GenderSeparator.props, GenderSeparator.name)

  val teamEvaluator = createRouter(context, 4, TeamEvaluator.props(self))
  val genderComboRouter = createRouter(context, 4, GenderCombinator.props(teamEvaluator))
  val naiveTeamBuilder = context.actorOf(NaiveTeamBuilder.props(genderComboRouter))
  var evals = List.empty[Int]
  var bestTeamComboSoFar = FinalTeamsWithValue(Nil, Int.MaxValue)
  var teamCount = 0
  var evaluatorsFinished = 0

  def receive = separatePlayers

  def separatePlayers: Receive = {
    case w@WeightVector(_) => teamEvaluator.routees.foreach(_.send(w,self))
    case FinalPlayersWithTeamNumber(players,meanVals,count) =>
      genderSeparator ! FinalPlayers(players, meanVals)
      teamCount = count
    case sp @ SeparatedPlayers(males,females,means) =>
      naiveTeamBuilder ! SeparatedPlayersWithCount(males, females, means, teamCount)
      context become buildTeams
  }

  def buildTeams: Receive = {
    case incomingTeams @ FinalTeamsWithValue(teams, eval) =>
      evals ::= eval
      if (eval < bestTeamComboSoFar.eval)
        bestTeamComboSoFar = incomingTeams
    case LastMessage =>
      evaluatorsFinished += 1
      if (evaluatorsFinished == 4) {
        val m = TreeMap(evals.groupBy(i => i).map(t => (t._1, t._2.size)).toArray: _*)
        master ! BestTeamComboAndEval(FinalTeamsLinesWithValue(new TeamsLines(bestTeamComboSoFar.teams), bestTeamComboSoFar.eval), m)
        println(s"this is the eval: ${bestTeamComboSoFar.eval}")
      }
  }
}