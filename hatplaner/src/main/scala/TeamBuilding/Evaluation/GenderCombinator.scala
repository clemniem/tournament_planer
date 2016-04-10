package TeamBuilding.Evaluation

import Import.{FinalPlayer, Team}
import Import.Types.{SubTeam, ValueVector}
import TeamBuilding.Evaluation.GenderCombinator.{SubTeamsToCombinate, FinalTeams}
import TeamBuilding.NaiveAlgorithm.NaiveTeamBuilder.LastMessage
import akka.actor.{Actor, ActorRef, Props}
import akka.routing.Router

import scala.util.Random

/**
  * Takes a List of male and female SubTeams and combines them to 100 different TeamCombos
  */
object GenderCombinator {
  def props(teamEvaluator: Router) = Props(new GenderCombinator(teamEvaluator))
  case class SubTeamsToCombinate(males: List[SubTeam], females: List[SubTeam], mean: ValueVector)
  case class FinalTeams(teams: List[Team], mean: ValueVector)
}

//receives List of SubTeams (m and f) and combines them randomly
class GenderCombinator(teamEvaluator: Router) extends Actor{
  var totalCount = 0
  def receive: Receive = {
    case SubTeamsToCombinate(males, females, mean) =>
      totalCount += 1
      for(i <- 0 until 100) {
        val subTeamTuples = Random.shuffle(males).zip(Random.shuffle(females))
        val listOfTeams: List[Team] = subTeamTuples.map(subTeamsCombinator).map(subTeamToTeam)
        teamEvaluator.route(FinalTeams(listOfTeams, mean), self)
      }
    case LastMessage =>
      println(s"fertig $totalCount ${self.path}")
      teamEvaluator.routees.foreach(actor => actor.send(LastMessage, self))
  }

  //Takes tuple of SubTeams (maleSub,femaleSb)
  def subTeamsCombinator(subTeamTuple: (SubTeam, SubTeam)): SubTeam = { ()
    subTeamTuple match {

      //decomposing of maleSubTeam and femaleSubTeam
      case (
        ((mCount, mStrength), mPlayers: List[FinalPlayer]),
        (((fCount, fStrength), fPlayers: List[FinalPlayer]))) =>

        // combines ((count,strength),players) of m and f
        ((mCount + fCount, mStrength + fStrength), mPlayers ++ fPlayers)

    }}

  //convert SubTeam to Team with meanStrength
  def subTeamToTeam(subTeam: SubTeam): Team = { subTeam match {
    case (((count: Int, strength: Int), players)) =>
      Team(strength / count, players)
  }
  }
}