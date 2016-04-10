package TeamBuilding

import Import._
import TeamBuilding.Evaluation.{GenderCombinator, TeamEvaluator}
import GenderCombinator.FinalTeams
import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import org.scalatest.{MustMatchers, WordSpecLike}
import scala.concurrent.duration._


/**
  * Created by yannick on 16.02.16.
  */
class TeamEvaluatorTest extends TestKit(ActorSystem("testSys"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with StopSystemAfterAll {

  "A TeamEvaluatorTest" must {

//    "evaluate a List of teams and send them back with the eval value" in {
//      import TeamEvaluator._
//      val teamEvaluator = system.actorOf(props(testActor))
//      val listOfTeams = List(
//        Team(6, List(
//          FinalPlayer("bob", "team", Female, Vector(10, 10, 10, 10), 5,Nil),
//          FinalPlayer("bob", "team", Female, Vector(20, 20, 20, 20), 1,Nil))),
//        Team(6, List(
//          FinalPlayer("bob", "team", Female, Vector(10, 10, 10, 10), 5,Nil),
//          FinalPlayer("bob", "team", Female, Vector(20, 20, 20, 20), 1,Nil))))
//      teamEvaluator ! FinalTeams(listOfTeams, Vector(15, 15, 15, 15, 6))
//      expectMsgPF(10.seconds) {
//        case FinalTeamsLinesWithValue(_, valueTest) => valueTest must be(24)
//      }
//    }
    "calculate the team mean vector when inputting the team vectors" in {
      import TeamEvaluator._
      val teamEvaluator: TestActorRef[TeamEvaluator] = TestActorRef(props(testActor))
      val testTeams = List(
        Team(6, List(
          FinalPlayer("A", "team", Female, Vector(10, 10, 10, 10), 5,Wish("A","B",1)::Nil),
          FinalPlayer("B", "team", Female, Vector(20, 20, 20, 20), 1,Wish("B","D",-1)::Nil),
          FinalPlayer("C", "qwe", Female, Vector(10, 10, 10, 10), 5,Wish("C","B",1)::Nil),
          FinalPlayer("D", "qwe", Female, Vector(20, 20, 20, 20), 1,Wish("D","B",-1)::Nil))),
        Team(3, List(
          FinalPlayer("bob1", "qwe", Female, Vector(10, 10, 10, 10), 5,Wish("bob1","bob2",1)::Nil),
          FinalPlayer("bob2", "wer", Female, Vector(20, 20, 20, 20), 1,Wish("bob2","B",-1)::Nil),
          FinalPlayer("bob3", "wer", Female, Vector(10, 10, 10, 10), 5,Wish("bob3","A",-1)::Nil),
          FinalPlayer("bob4", "wer", Female, Vector(20, 20, 20, 20), 1,Wish("bob4","C",-1)::Nil))))
      val te = teamEvaluator.underlyingActor
      val testTeamNoWish = Team(6, List(
        FinalPlayer("bob1", "team", Female, Vector(10, 10, 10, 10), 5,Nil),
        FinalPlayer("bob", "team", Female, Vector(20, 20, 20, 20), 1,Wish("bob","bob1",1)::Nil))) ::Nil
      val x = te.teamToMean(Team(6, List(
        FinalPlayer("bob", "team", Female, Vector(10, 10, 10, 10), 5,Nil),
        FinalPlayer("bob", "team", Female, Vector(20, 20, 20, 20), 1,Nil))))
      x match {
        case Vector(15, 15, 15, 15, 6) =>
      }
      val y = te.diffToMean(Vector(10, 10, 10, 10), Vector(5, 15, 5, 15))
      y match {
        case a1 =>
      }
      val malus = te.sameTeamMalus(testTeams)
      malus match {
        case a2 =>
          a2 must be(51)
      }
      val wishTest = te.wishBonusMalus(testTeams)
      wishTest match {
        case a3 =>
          println(s"Wishes: $a3")
          a3 must be(4)
      }
      val nilWishTest = te.wishBonusMalus(testTeamNoWish)
      nilWishTest match {
        case b1 =>
          println(b1)
          b1 must be(1)
      }
      val evaluateTest = te.teamToMean(Team(10,List(
        FinalPlayer("A", "team", Female, Vector(10, 10, 10, 10), 5,Wish("A","B",1)::Nil),
        FinalPlayer("B", "team", Female, Vector(20, 20, 20, 20), 1,Wish("B","D",-1)::Nil))))
      println(evaluateTest)
    }


  }
}