package TeamBuilding.GenderSeparation

import Import.ImportOptimizer.ImportOptimizer.{FinalPlayers}
import Import.Types.ValueVector
import Import.{FinalPlayer, Male}
import akka.actor.{Actor, Props}




/**
  * Created by yannick on 16.02.16.
  */
object GenderSeparator {
  val props = Props(new GenderSeparator)
  val name = "gender-separator"
  case class SeparatedPlayers(malePlayers: List[FinalPlayer], femalePlayers: List[FinalPlayer], meanVals: ValueVector)
}

class GenderSeparator extends Actor{
  import GenderSeparator._

  def genderSplit(player: List[FinalPlayer]): (List[FinalPlayer], List[FinalPlayer]) = {
    player.partition(_.gender == Male)
  }

  def receive: Receive = {
    case FinalPlayers(player, mean) =>
      val playerTuple = genderSplit(player)
      sender ! SeparatedPlayers(playerTuple._1, playerTuple._2, mean)
      println(s"genderseperator received players")
  }
}