package Import

import Import.ImportOptimizer.ImportOptimizer.MeanVals
import Import.ImportOptimizer.PlayerMeanFiller
import akka.actor.ActorSystem
import akka.testkit.{TestKit, ImplicitSender}
import org.scalatest.{WordSpecLike, MustMatchers}

/**
  * Created by yannick on 14.02.16.
  */
class PlayerMeanFillerTest extends TestKit(ActorSystem("testSys"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with StopSystemAfterAll {

  "A PlayerMeanFiller" must {
    "send back the same values if non are empty" in {
      import PlayerMeanFiller._
      val meanFiller = system.actorOf(props)
      meanFiller ! FillMeanVals(
        MeanVals(Vector(3,3,3,3,3)), Player("test","team", Female, Vector(4,3,2,1,1),"a","s"),Vector(1,1,1,1,1))
      expectMsg(FinalPlayerMessage(
        FinalPlayer("test","team", Female, Vector(4,3,2,1,1),15,List(Wish("test","a",-1),Wish("test","s",1)))))
    }

    "replace 0 values with the mean values" in {
      import PlayerMeanFiller._
      val meanFiller = system.actorOf(props)
      meanFiller ! FillMeanVals(MeanVals(Vector(3,0,0,3,3)), Player("test","team", Female, Vector(4,10,10,1,0),"a","s"),Vector(1,1,1,1,1))
      expectMsg(FinalPlayerMessage(FinalPlayer("test","team", Female, Vector(4,10,10,1,3),32,List(Wish("test","a",-1),Wish("test","s",1)))))
    }
  }
}