package Import

import Import.ImportOptimizer.MeanCalculator
import akka.actor.ActorSystem
import akka.testkit.{TestKit, ImplicitSender}
import org.scalatest.{WordSpecLike, MustMatchers}

/**
  * Created by yannick on 14.02.16.
  */
class MeanCalculator$Test extends TestKit(ActorSystem("testSys"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with StopSystemAfterAll {

  "A MeanCalculator" must {
    "calculate the mean of given integer values and send it back to the sender" in {
      import MeanCalculator._
      val meanCalc = system.actorOf(props)
      meanCalc ! ListToCalculate(0, List(Vector(1,1,1,1),Vector(2,1,1,1),Vector(3,1,1,1),Vector(4,1,1,1)))
      expectMsg(Mean(0, 250))
    }
    "return 0 as mean if all input is 0" in {
      import MeanCalculator._
      val meanCalc = system.actorOf(props)
      meanCalc ! ListToCalculate(0, List(Vector(0,1,1,1),Vector(0,1,1,1),Vector(0,1,1,1),Vector(0,1,1,1)))
      expectMsg(Mean(0, 0))
    }
    "dont take 0 values into calculation" in {
      import MeanCalculator._
      val meanCalc = system.actorOf(props)
      meanCalc ! ListToCalculate(0, List(Vector(0,1,1,1),Vector(0,1,1,1),Vector(0,1,1,1),Vector(5,1,1,1)))
      expectMsg(Mean(0, 500))
    }
  }
}