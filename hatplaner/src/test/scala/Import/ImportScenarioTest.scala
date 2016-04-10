package Import

import Import.ImportOptimizer.ImportOptimizer.FinalPlayers
import RestConnection.TeamRequest
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{MustMatchers, WordSpecLike}

/**
  * Created by yannick on 15.02.16.
  */
class ImportScenarioTest extends TestKit(ActorSystem("testSys"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with StopSystemAfterAll {

  "An ImportManger" must {
    "import a valid file, send the meanValues and all players" in {
      import ImportManager._
      import Import.Attributes._
      val importManger = system.actorOf(props(testActor), name)
      importManger ! TeamRequest(TestData.validPlayers2, 2,Vector.empty[Int])
      expectMsgPF(){
        case FinalPlayers(players, meanVal) =>
          println(meanVal.toString)

          players must have length 8
          meanVal(exp) must be(2500)
          meanVal(agility) must be(525)
          meanVal(fitness) must be(17162)
          meanVal(handler) must be(1020)
          meanVal(receiver) must be(100)
          players foreach(p => p.name match {
            case "Yannick" => p.values(exp) must be(26)
            case any =>
          })
      }
    }
  }
}