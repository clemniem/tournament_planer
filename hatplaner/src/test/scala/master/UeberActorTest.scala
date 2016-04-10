package master

import Import._
import RestConnection.TeamRequest
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{MustMatchers, WordSpecLike}
import scala.concurrent.duration._


/**
  * Created by yannick on 16.02.16.
  */
class UeberActorTest extends TestKit(ActorSystem("testSys"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with StopSystemAfterAll {

  "An UeberActorMust" must {
//    "read in an csv file and send back the best team" in {
//      val ueberActor = system.actorOf(UeberActor.props, UeberActor.name)
//      ueberActor ! TeamRequest(TestData.validPlayers, 2,Vector.empty[Int])
//      expectMsgPF(13.seconds) {
//        case a1 => println("ueberTest " + a1)
//      }
//    }
    "tell me where the values get so big" in {
      val ueberActor = system.actorOf(UeberActor.props, UeberActor.name)
      ueberActor ! TeamRequest(TestData.validPlayersShort, 2,Vector.empty[Int])
      expectMsgPF(13.seconds) {
        case a1 => println("ueberTest " + a1)
      }
    }

  }
}