package Import

import Import.FileImport.{CsvLineImporter}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{WordSpecLike, MustMatchers}

/**
  * Created by yannick on 13.02.16.
  */
class CsvLineImporterTest extends TestKit(ActorSystem("testSys"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with StopSystemAfterAll {

  "A CsvLineImporter" must {
    "create a player from a valid line" in {
      import CsvLineImporter._
      val lineImporter = system.actorOf(props)
      lineImporter ! InputLine("Clemens,Tiefseetaucher,m,7,7,7,7,7,Yan,Nik")
      expectMsg(Player("Clemens", "Tiefseetaucher", Male, Vector(7, 7, 7,7,7),"Yan","Nik"))
    }

    "send the last message if the input was not valid" in {
      import CsvLineImporter._
      val lineImporter = system.actorOf(props)
      lineImporter ! InputLine("defect line")
      expectMsg(ErrorInLine("defect line"))
    }

    "send an error message if the name is empty" in {
      import CsvLineImporter._
      val lineImporter = system.actorOf(props)
      lineImporter ! InputLine(",team,m,27,10,179,7,3,a,b")
      expectMsg(ErrorInLine(",team,m,27,10,179,7,3,a,b"))
    }

    "fill missing fields with default 0" in {
      import CsvLineImporter._
      val lineImporter = system.actorOf(props)
      lineImporter ! InputLine("Yannick,,m,,,,,,cle,mens")
      expectMsg(Player("Yannick","",Male,Vector(0,0,0,0,0),"cle","mens"))
    }
  }
}