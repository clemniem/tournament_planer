package controllers

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import org.joda.time.LocalDate
import spray.json.DefaultJsonProtocol
case class ResultTeamsAndEval(teams: List[String], eval: Seq[(Int, Int)])
case class TeamRequest(players: List[String], teamCount: Int, weightVector: Vector[Int])
case class TournamentRequest(teams: List[String], startTime: String, endTime: String, gameTime: Int, pauseTime: Int, fields: Int, gameMode: Int, days: Int)
case class BrokenLines(broken: List[String])
case class FinishedSchedule(slots: List[String])

//mapping json to case class and back
trait Protocols extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val tournamentRequest = jsonFormat8(TournamentRequest.apply)
  implicit val brokenLine = jsonFormat1(BrokenLines.apply)
  implicit val resultData = jsonFormat2(ResultTeamsAndEval.apply)
  implicit val getTeamRequest = jsonFormat3(TeamRequest.apply)
  implicit val finishedTournamentResponse = jsonFormat1(FinishedSchedule.apply)
}