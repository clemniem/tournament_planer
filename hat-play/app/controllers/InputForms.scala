package controllers

import org.joda.time.LocalDate
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

/**
  * Maps html forms to case classes
  */
trait InputForms {

  trait InputForm {
    val teams: Int
    val weightVector: Vector[Int]
  }

  trait TournamentRequirements {
    val startTime: String
    val endTime: String
    val gameTime: Int
    val pauseTime: Int
    val fields: Int
    val gameMode: Int
    val days: Int
  }

  case class TeamForm(teams: Int, exp: Int, agi: Int, fit: Int, handling: Int, receiver: Int, teamMalus: Int, wish: Int) extends InputForm {
    val weightVector= Vector(exp, agi, fit, handling, receiver, teamMalus, wish)
  }

  val teamsForm = Form(
    mapping(
      "teams" -> number(min = 2, max = 100),
      "exp" -> number(min = 0, max = 100),
      "agi" -> number(min = 0, max = 100),
      "fit" -> number(min = 0, max = 100),
      "handling"  -> number(min = 0, max = 100),
      "receiver"  -> number(min = 0, max = 100),
      "teamMalus"  -> number(min = 0, max = 100),
      "wish"  -> number(min = 0, max = 100)
    )(TeamForm.apply)(TeamForm.unapply)
  )

  case class TeamTournamentForm(teams: Int, startTime: String, endTime: String, gameTime: Int, pauseTime: Int,
                                fields: Int, gameMode: Int, days: Int) extends InputForm with TournamentRequirements {
    val weightVector = Vector(150,150,150,150,150,150,150)
  }

  val teamTournamentForm = Form(
    mapping(
      "teams" -> number(min = 2, max = 100),
      "startTime" -> nonEmptyText,
      "endTime" -> nonEmptyText,
      "gameTime" -> number(15, 60),
      "pauseTime" -> number(5, 20),
      "fields" -> number(1, 100),
      "gameMode" -> number(0, 2),
      "days" -> number(min = 1, max = 7)
    )(TeamTournamentForm.apply)(TeamTournamentForm.unapply)
  )

  case class TournamentFrom(startTime: String, endTime: String, gameTime: Int, pauseTime: Int, fields: Int,
                            gameMode: Int, days: Int) extends InputForm with TournamentRequirements{
    val teams = 0
    val weightVector = Vector(150,150,150,150,150,150,150)
  }

  val tournamentForm = Form(
    mapping(
      "startTime" -> nonEmptyText,
      "endTime" -> nonEmptyText,
      "gameTime" -> number(15, 60),
      "pauseTime" -> number(5, 20),
      "fields" -> number(1, 100),
      "gameMode" -> number(0, 2),
      "days" -> number(min = 1, max = 7)
    )(TournamentFrom.apply)(TournamentFrom.unapply)
  )

}

