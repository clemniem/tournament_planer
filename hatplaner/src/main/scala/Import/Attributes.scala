package Import

import Import.Types.{Strength, ValueVector}

import scala.collection.mutable.ListBuffer

/**
  * needed Datatypes
  */
case class Player(name: String, team: String, gender: Gender, values: ValueVector, posWish:String, negWish:String)
case class FinalPlayer(name: String, team: String, gender: Gender, values: ValueVector, finalStrength: Strength, wishes:List[Wish])
case class Team(val teamMeanStrength: Strength, players: List[FinalPlayer])
case class Wish(wisher: String, wished: String, bonus:Int)

class TeamsLines(val teams: List[Team]) {
  //gives Back a List of PlayerNames, separated by Teams for write to .csv
  def toLines: List[String] = {
    var teamID = 1
    var lines = new ListBuffer[String]()
    for (team <- teams) {
      lines += ";"
      lines += s"Team $teamID (${team.teamMeanStrength})"
      for (player <- team.players){
        lines += player.name
      }
      lines += ":"
      teamID += 1
    }
    lines.toList
  }
}

//Player Attributes and the corresponding Index for ValueVectors
object Attributes{
  val exp = 0
  val agility = 1
  val fitness = 2
  val handler = 3
  val receiver = 4
}

object Types {
  type Strength = Int
  type ValueVector = Vector[Int]
  //Subteam = ((),)
  type SubTeam = ((Int,Int),List[FinalPlayer])
}

//Gender represented as +1/-1 for calculation of TeamEquality
sealed trait Gender
case object Male extends Gender
case object Female extends Gender

object Caster{
  def toGender(gender: String): Gender = {
    gender match {
      case "male" => Male
      case "m" => Male
      case "female" => Female
      case "f" => Female
      case "w" => Female
    }
  }
}