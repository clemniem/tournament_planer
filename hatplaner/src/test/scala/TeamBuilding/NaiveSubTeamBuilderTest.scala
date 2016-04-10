package TeamBuilding

import Import.{Male, Female, FinalPlayer, StopSystemAfterAll}
import TeamBuilding.NaiveAlgorithm.NaiveSubTeamBuilder
import akka.actor.ActorSystem
import akka.testkit.{TestKit, ImplicitSender}
import org.scalatest.{WordSpecLike, MustMatchers}

/**
  * Created by yannick on 16.02.16.
  */
class NaiveSubTeamBuilderTest extends TestKit(ActorSystem("testSys"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with StopSystemAfterAll {

  "A NaiveSubteamBuilder" must {
    "create a List of Subteams from a List of players" in {
      import NaiveSubTeamBuilder._
      val naiveSubTeamBuilder = system.actorOf(props)
      val finalPlayerList = List(
        FinalPlayer("bob", "team", Female, Vector(4, 3, 2, 1), 1,Nil),
        FinalPlayer("bob", "team", Female, Vector(4, 3, 2, 1), 2,Nil),
        FinalPlayer("bob", "team", Female, Vector(4, 3, 2, 1), 3,Nil),
        FinalPlayer("bob", "team", Female, Vector(4, 3, 2, 1), 4,Nil),
        FinalPlayer("bob", "team", Female, Vector(4, 3, 2, 1), 5,Nil),
        FinalPlayer("bob", "team", Female, Vector(4, 3, 2, 1), 6,Nil),
        FinalPlayer("svenjamina", "team", Female, Vector(4, 3, 2, 1), 7,Nil),
        FinalPlayer("alice", "team", Female, Vector(4, 3, 2, 1), 8,Nil))

      naiveSubTeamBuilder ! PlayersWithTeamNumber(finalPlayerList, 4)
      expectMsgPF(){
        case SubTeams(_, list) =>
          list must have length 4
          list.head._2 must have length 2
      }
    }
  }
}