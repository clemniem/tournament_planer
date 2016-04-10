package TeamBuilding.NaiveAlgorithm

import Import.Types.SubTeam
import Import.{FinalPlayer, Gender}
import TeamBuilding.NaiveAlgorithm.NaiveSubTeamBuilder.{PlayersWithTeamNumber, SubTeams}
import akka.actor.{Actor, Props}
/**
  * Created by yannick on 16.02.16.
  */
object NaiveSubTeamBuilder {
  val props = Props(new NaiveSubTeamBuilder)

  case class SubTeams(gender: Gender, lSubTeam: List[SubTeam])
  case class PlayersWithTeamNumber(players: List[FinalPlayer], teams: Int)
}

class NaiveSubTeamBuilder extends Actor {

  def receive: Receive = {
    case PlayersWithTeamNumber(players, number) => sender ! SubTeams(players.head.gender, buildSubTeam(players,List.fill(number)((0,0),Nil)))
  }

  //Takes first Player from PlayersList and puts him in the smallest SubTeam with the weakest strength
  //subTeamList is ordered, so that the smallest/weakest team is the head of SubTeamList
  def buildSubTeam(players: List[FinalPlayer], subTeamList: List[SubTeam]): List[SubTeam] = {
    players match {
      case Nil => subTeamList
      case newPlayer :: psTail =>
        subTeamList match {
          case ((count,strength),(subPlayers)) :: subTeamTail =>
            //recursive call with tail of playersList and updated SubTeamList (new Head and sortedBy(count,strength)
            buildSubTeam(psTail, (((count + 1,strength + newPlayer.finalStrength), newPlayer :: subPlayers) :: subTeamTail).sortBy(_._1))
        }
    }

  }


}