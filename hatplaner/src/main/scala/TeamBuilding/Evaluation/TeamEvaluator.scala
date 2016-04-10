package TeamBuilding.Evaluation

import Import.{Wish, TeamsLines, Team}
import Import.Types.ValueVector
import GenderCombinator.FinalTeams
import TeamBuilding.Evaluation.TeamEvaluator.{FinalTeamsWithValue, FinalTeamsLinesWithValue}
import TeamBuilding.NaiveAlgorithm.NaiveTeamBuilder.LastMessage
import akka.actor.{Actor, ActorRef, Props}
import master.UeberActor.WeightVector

import scala.annotation.tailrec
import scala.concurrent.Future

/**
  * Created by yannick on 16.02.16.
  */
object TeamEvaluator {
  def props(master: ActorRef) = Props(new TeamEvaluator(master))
  case class FinalTeamsWithValue(teams:List[Team],eval: Int)
  case class FinalTeamsLinesWithValue(teams: TeamsLines, eval: Int)

}

class TeamEvaluator(master: ActorRef) extends Actor {
  var weightVector = Vector(100,100,100,100,100,100,100)
  var combinatorsFinished = 0
  val maxWait = context.system.settings.config.getInt("akka.calculation-complexity")*100

  def receive: Receive = {
    case WeightVector(w) => weightVector = w
    case FinalTeams(teams, mean) =>
      master ! evaluateTeam(teams, mean)
    case LastMessage => combinatorsFinished += 1
      if (combinatorsFinished == 4) {
        println("teamEvaluator finished")
        master ! LastMessage
      }
  }

  def sameTeamMalus(teams: List[Team]): Int = {
    val nameListss = teams.map(t => t.players.map(fp => fp.team))
    val nameAccMap = nameListss.map(team => team.groupBy(identity).mapValues(_.size))
    val malis = nameAccMap.flatMap(_.values).filter(_ > 1)
    //todo check if weight is right
    malis.map(m => m * m).sum * weightVector(5)
  }

  //creates List of Members for each Team and checks if wishes came true
  def wishBonusMalus(teams: List[Team]): Int = {
    val sortedPlayerNamess = teams.map(_.players.map(_.name))
    val origWishes = teams.flatMap(_.players).flatMap(_.wishes)
    @tailrec
    def wishBonusMalusAcc(playerss: List[List[String]], wishes: List[Wish], acc: Int): Int =
      (playerss,wishes) match {
        case (_, Nil) => acc
        //sums up wishes that came true and the unfullfilled ones
        case (Nil ,rs) => acc + rs.map(w => -w.bonus).sum
        case (plns :: plnss,_) =>
          var count = acc
          var restWishes:List[Wish] = Nil
          for (wish <- wishes) {
            if (plns.contains(wish.wisher) && plns.contains(wish.wished)) {
              count += wish.bonus
            } else {
              restWishes ::= wish
            }
          }
          wishBonusMalusAcc(plnss,restWishes, count)
      }
    //todo check if weight is right
    wishBonusMalusAcc(sortedPlayerNamess,origWishes,0) * weightVector(6)
  }

  //returns TeamsLines and (eval= errorSum + sameTeamMalus)
  def evaluateTeam(teams: List[Team], mean: ValueVector): FinalTeamsWithValue = {
    FinalTeamsWithValue(teams, errorSum(teams, mean) + sameTeamMalus(teams) + wishBonusMalus(teams))
  }

  def errorSum(teams: List[Team], mean: ValueVector): Int = {
    teams.map(teamToMean).map(teamMean => evalDiffVector(diffToMean(teamMean, mean))).sum
  }

  def teamToMean(team: Team): ValueVector = {
    team.players.map(p => p.values).reduce((v1, v2) => (v1, v2).zipped.map(_ + _)).map(100 * _ / team.players.size) :+ team.teamMeanStrength
  }

  def diffToMean(teamMean: Vector[Int], mean: Vector[Int]): Vector[Int] = {
    import scala.math._
    teamMean.zip(mean).map(t => abs(t._1 - t._2))
  }

  def evalDiffVector(diffVector: Vector[Int]): Int = {
    //                         exp, agi, fit,hand, rec
    val modifierVector = Vector(2, 1, 1, 1, 1).zip(weightVector).map(value => value._1 * value._2)
    diffVector.zip(modifierVector).map(t => t._1 * t._2).sum
  }
}