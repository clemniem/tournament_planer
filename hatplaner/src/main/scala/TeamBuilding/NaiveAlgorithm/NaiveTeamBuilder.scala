package TeamBuilding.NaiveAlgorithm

import Import.Types.{SubTeam, ValueVector}
import Import.{Female, Male, Routing, Team}
import TeamBuilding.Evaluation.{GenderCombinator, TeamEvaluator}
import GenderCombinator.SubTeamsToCombinate
import TeamBuilding.GenderSeparation.GenderSeparator.SeparatedPlayers
import TeamBuilding.NaiveAlgorithm.NaiveSubTeamBuilder.{PlayersWithTeamNumber, SubTeams}
import TeamBuilding.NaiveAlgorithm.NaiveTeamBuilder.LastMessage
import TeamBuilding.SeparatedPlayersWithCount
import TeamEvaluator.FinalTeamsLinesWithValue
import akka.actor.{Actor, Props}
import akka.routing.Router

import scala.util.Random

/**
  * Created by yannick on 16.02.16.
  */
object NaiveTeamBuilder {
  def props(r: Router) = Props(new NaiveTeamBuilder(r))
  case class Teams(teams: List[Team])
  object LastMessage
}

class NaiveTeamBuilder(genderCombinator: Router) extends Actor with Routing {
  val subTeamRouter = createRouter(context, 4, NaiveSubTeamBuilder.props)
  var meanVals: ValueVector = null
  val complexity = context.system.settings.config.getInt("akka.calculation-complexity")

  var maleSubTeamsCombinations: List[List[SubTeam]] = Nil
  var femaleSubTeamsCombinations: List[List[SubTeam]] = Nil

  //sends SubTeamsCombinations to genderCombinator if every SubTeam is received
  def sendIfReady = {
    if(maleSubTeamsCombinations.size + femaleSubTeamsCombinations.size == 2 * complexity) {
      for (males <- maleSubTeamsCombinations;
           females <- femaleSubTeamsCombinations) {
        genderCombinator.route(SubTeamsToCombinate(males, females, meanVals), self)
      }
      genderCombinator.routees foreach { genderCombo =>
        genderCombo.send(LastMessage, self)
      }
    }
  }

  def receive: Receive = {
    case sp @ SeparatedPlayersWithCount(males,females, mean, teamCount) =>
      println(complexity)
      meanVals = mean
      for (i <- 0 until complexity) {
        //shuffles males and females randomly for naive algorithm to build different naiveSubTeam
        subTeamRouter.route(PlayersWithTeamNumber(Random.shuffle(males), teamCount),self)
        subTeamRouter.route(PlayersWithTeamNumber(Random.shuffle(females), teamCount),self)
      }

    //adds SubTeam to SubTeamsCombinations and checks if all combinations are received (for m and f separately)
    case SubTeams(Male, subTeams) => maleSubTeamsCombinations ::= subTeams
      sendIfReady
    case SubTeams(Female, subTeams) => femaleSubTeamsCombinations ::= subTeams
      sendIfReady
  }


}