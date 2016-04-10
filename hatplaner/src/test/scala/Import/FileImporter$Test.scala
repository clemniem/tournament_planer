package Import

import Import.FileImport.FileImporter
import RestConnection.TeamRequest
import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.{MustMatchers, WordSpecLike}

/**
  * Created by yannick on 13.02.16.
  */
class FileImporter$Test extends TestKit(ActorSystem("testSys"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {

  "An FileImporter" must {
     "send one broken line back to the reference given to him" in {
      import FileImporter._
      val importer = system.actorOf(props(testActor))
      importer ! TeamRequest(TestData.brokenPlayers, 2,Vector.empty[Int])
      expectMsg(BrokenLines(List("Clemens")))
    }

    "send all broken lines back to the reference given to him" in {
      import FileImporter._
      val importer = system.actorOf(props(testActor))
      importer ! TeamRequest(TestData.brokenPlayersTwo, 2,Vector.empty[Int])
      expectMsgPF() {
        case BrokenLines(l: List[Any]) =>
          l must have length 3
      }
    }
  }
}