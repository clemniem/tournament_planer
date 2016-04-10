package TeamBuilding

import Import.ImportOptimizer.ImportOptimizer.{FinalPlayers}
import Import.{Male, Female, FinalPlayer, StopSystemAfterAll}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import master.UeberActor.FinalPlayersWithTeamNumber
import org.scalatest.{MustMatchers, WordSpecLike}

import scala.concurrent.duration._

/**
  * Created by yannick on 16.02.16.
  */
class TeamBuildingMasterTest  extends TestKit(ActorSystem("testSys"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with StopSystemAfterAll {

  "A TeamBuildingMaster" must {
    "send back the best selected team" in {
      val teamBuildingMaster = system.actorOf(TeamBuildingMaster.props(testActor))
      val finalPlayerList = List(
        FinalPlayer("bob", "team", Female, Vector(4, 3, 2, 1), 1,Nil),
        FinalPlayer("bob", "team", Male, Vector(4, 3, 2, 1), 2,Nil),
        FinalPlayer("bob", "team", Female, Vector(4, 3, 2, 1), 3,Nil),
        FinalPlayer("bob", "team", Male, Vector(4, 3, 2, 1), 4,Nil),
        FinalPlayer("bob", "team", Female, Vector(4, 3, 2, 1), 5,Nil),
        FinalPlayer("bob", "team", Male, Vector(4, 3, 2, 1), 6,Nil),
        FinalPlayer("svenjamina", "team", Female, Vector(4, 3, 2, 1), 7,Nil),
        FinalPlayer("alice", "team", Male, Vector(4, 3, 2, 1), 8,Nil))
      teamBuildingMaster ! FinalPlayersWithTeamNumber(finalPlayerList, Vector(4, 3, 2, 1, 4),2)
      expectMsgPF(12.seconds) {
        case a => println(a)
      }
    }
  }
}