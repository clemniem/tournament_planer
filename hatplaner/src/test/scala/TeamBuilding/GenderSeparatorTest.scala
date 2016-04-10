package TeamBuilding

import Import.ImportOptimizer.ImportOptimizer.{FinalPlayers}
import Import.{Male, Female, FinalPlayer, StopSystemAfterAll}
import TeamBuilding.GenderSeparation.GenderSeparator
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{MustMatchers, WordSpecLike}

/**
  * Created by yannick on 16.02.16.
  */
class GenderSeparatorTest extends TestKit(ActorSystem("testSys"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with StopSystemAfterAll {

  "A GenderSeparator" must {
    "separate a list of players in two lists separated by gender" in {
      import GenderSeparator._
      val genderSeparator = system.actorOf(props)
      val finalPlayerList = List(
        FinalPlayer("alice", "team", Female, Vector(4, 3, 2, 1), 4,Nil),
        FinalPlayer("bob", "team", Male, Vector(4, 3, 2, 1), 4,Nil),
        FinalPlayer("svenjamina", "team", Female, Vector(4, 3, 2, 1), 4,Nil))
      genderSeparator ! FinalPlayers(finalPlayerList, Vector(4, 3, 2, 1))
      expectMsgPF(){
        case SeparatedPlayers(ms,fs,_) =>
          ms must have length 1
          fs must have length 2
          ms.head.name must be("bob")
          for (player <- fs) {
            player.name must matchPattern {
              case "alice" =>
              case "svenjamina" =>
            }
          }
      }
    }
  }
}