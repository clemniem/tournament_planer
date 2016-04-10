package Import.ImportOptimizer

import Import.ImportOptimizer.ImportOptimizer.MeanVals
import Import.Types.{ValueVector, Strength}
import Import.{Wish, FinalPlayer, Player}
import akka.actor.{Actor, Props}

import scala.math._

/**
  * Created by yannick on 14.02.16.
  */
object PlayerMeanFiller {
  val props = Props(new PlayerMeanFiller)

  case class FillMeanVals(mean: MeanVals, player: Player, weightVector: Vector[Int])
  case class FinalPlayerMessage(player: FinalPlayer)
}
class PlayerMeanFiller extends Actor with ValOrMean with CalculateStrength with stringsToWishes {
  import ImportOptimizer._
  import PlayerMeanFiller._
  def receive: Receive = {
    case FillMeanVals(MeanVals(meanVector), p @ Player(name,team,gender,playerVector,posW,negW), weightVector) =>
      //fills PlayerValueVector with meanVals if entry is null
      val filledPlayerVector = playerVector zip meanVector map { case (value, mean) => valOrMean(value, mean) }
      //calculates FinalStrength for each Player (converts Player to FinalPlayer)
      sender ! FinalPlayerMessage(
        FinalPlayer(name, team, gender,
          filledPlayerVector,
          calculateSumStrength(filledPlayerVector, weightVector),
          stringsToWishes(name,posW,negW)))
  }
}

trait stringsToWishes{
  def stringsToWishes(name:String, pos:String, neg:String): List[Wish] = (pos,neg) match {
    case ("","") => List(Wish(name,name,0))
    case ("",n)  => List(Wish(name,n,1))
    case (p,"")  => List(Wish(name,p,-1))
    case (_,_)   => List(Wish(name,pos,-1),Wish(name,neg,1))

  }
}


trait ValOrMean {
  def valOrMean(value: Int, mean: Int): Int = {
    if(value == 0) mean else value
  }
}

trait CalculateStrength {

  def calculateSumStrength(playerVector: ValueVector, weightVector: ValueVector): Strength = {
    //todo nice strength algorithm
    val modifierVector = Vector(2, 1, 1, 1, 1)
    playerVector.zip(weightVector).map(value => value._1 * value._2).zip(modifierVector).map(value => value._1 * value._2).sum
  }

  def calculateSquareStrength(playerVector: ValueVector, meanVector: ValueVector, weightVector: ValueVector): Strength = {
    val diffVector = playerVector.zip(meanVector).map(t => pow(t._1 - t._2,2).toInt)
    //                          exp, agi, fit,hand, rec
    val modifierVector = Vector(2, 1, 1, 1, 1)
    diffVector.zip(weightVector).map(value => value._1 * value._2).zip(modifierVector).map(value => value._1 * value._2).sum
  }

}