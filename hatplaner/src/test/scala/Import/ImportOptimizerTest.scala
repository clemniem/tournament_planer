package Import

import Import.FileImport.FileImporter.PlayerList
import akka.actor.ActorSystem
import akka.testkit.{TestKit, ImplicitSender}
import org.scalatest.{WordSpecLike, MustMatchers}
import scala.concurrent.duration._



/**
  * Created by yannick on 14.02.16.
  */
class ImportOptimizerTest extends TestKit(ActorSystem("testSys"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with StopSystemAfterAll {

  "An ImportOptimizer" must {
    "send back a MeanValues object for given input values " in {
      import ImportOptimizer.ImportOptimizer._
      val importOptimizer = system.actorOf(props(testActor))
      importOptimizer ! PlayerListWithWeightVector(PlayerList(List(
        Player("s", "s", Male, Vector(1,1,0,1,1),"a","d"),
        Player("top", "", Male, Vector(3,3,5,3,1),"","neg"))),Vector(100,100,100,100,100))
      expectMsgPF() {
        case FinalPlayers(players, meanValPlayer) =>
          players must have length 2
          for (player <- players) {
            player must matchPattern {
              case FinalPlayer("s", "s", _, Vector(1,1,500,1,1),50500,List(Wish("s","a",-1),Wish("s","d",1))) =>
              case ff@FinalPlayer("top" , _, _, Vector(3,3,5,3,1),1800,List(Wish("top","neg",1))) =>
            }
            println(s"Wishestest: $player")
          }
      }
    }
  }
}