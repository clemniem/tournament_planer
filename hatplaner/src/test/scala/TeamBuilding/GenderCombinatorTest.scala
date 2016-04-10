package TeamBuilding

import Import.{Male, Female, FinalPlayer, StopSystemAfterAll}
import TeamBuilding.Evaluation.GenderCombinator
import TeamBuilding.GenderSeparation.GenderSeparator
import GenderSeparator.SeparatedPlayers
import akka.actor.ActorSystem
import akka.testkit.{TestKit, ImplicitSender}
import org.scalatest.{WordSpecLike, MustMatchers}


/**
  * Created by yannick on 16.02.16.
  */
class GenderCombinatorTest extends TestKit(ActorSystem("testSys"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with StopSystemAfterAll {

  "A GenderSeparator" must {
    "separate a list of players in two lists separated by gender" in {
      import GenderCombinator._
      val genderCombinator = system.actorOf(props(testActor))
      val females = List(
        ((2,6),List(
          FinalPlayer("bob","team",Female,Vector(4, 3, 2, 1),5,Nil),
          FinalPlayer("bob","team",Female,Vector(4, 3, 2, 1),1,Nil))),
        ((2,8),List(
          FinalPlayer("bob","team",Female,Vector(4, 3, 2, 1),6,Nil),
          FinalPlayer("bob","team",Female,Vector(4, 3, 2, 1),2,Nil))),
        ((2,10),List(
          FinalPlayer("svenjamina","team",Female,Vector(4, 3, 2, 1),7,Nil),
          FinalPlayer("bob","team",Female,Vector(4, 3, 2, 1),3,Nil))),
        ((2,12),List(
          FinalPlayer("alice","team",Female,Vector(4, 3, 2, 1),8,Nil),
          FinalPlayer("bob","team",Female,Vector(4, 3, 2, 1),4,Nil))))
      val males = List(
        ((2,6),List(FinalPlayer("bob","team",Male,Vector(4, 3, 2, 1),5,Nil), FinalPlayer("bob","team",Male,Vector(4, 3, 2, 1),1,Nil))),
        ((2,8),List(FinalPlayer("bob","team",Male,Vector(4, 3, 2, 1),6,Nil), FinalPlayer("bob","team",Male,Vector(4, 3, 2, 1),2,Nil))),
        ((2,10),List(FinalPlayer("svenjamina","team",Male,Vector(4, 3, 2, 1),7,Nil), FinalPlayer("bob","team",Male,Vector(4, 3, 2, 1),3,Nil))),
        ((2,12),List(FinalPlayer("alice","team",Male,Vector(4, 3, 2, 1),8,Nil), FinalPlayer("bob","team",Male,Vector(4, 3, 2, 1),4,Nil))))
      genderCombinator ! SubTeamsToCombinate(males, females, Vector(4,3,2,1))
      Thread.sleep(1000)
      expectMsgPF(){
        case FinalTeams(teams, mean) =>
          teams.size must be(4)
          teams.head.players must have length 4

      }
    }
  }
}