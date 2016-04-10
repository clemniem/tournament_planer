package Import.ImportOptimizer

import Import.FileImport.FileImporter.PlayerList
import Import.ImportOptimizer.ImportOptimizer.{FinalPlayers, MeanVals}
import Import.ImportOptimizer.PlayerMeanFiller.{FinalPlayerMessage, FillMeanVals}
import Import.Types.{Strength, ValueVector}
import Import.{PlayerListWithWeightVector, FinalPlayer, Routing, Player}
import akka.actor.{Actor, ActorRef, Props}

import scala.collection.immutable.TreeMap

import scala.collection.immutable.TreeMap

/**
  * The ImportOptimizer delegates the player values to the MeanCalculator and the PlayerMeanFiller to
  * create the final players and the mean player values
  */
object ImportOptimizer {
  val name = "import-optimizer"
  def props(master: ActorRef) = Props(new ImportOptimizer(master))

  case class MeanVals(meanVals: ValueVector)
  case class FinalPlayers(players: List[FinalPlayer], meanVals: ValueVector)
}

class ImportOptimizer(master: ActorRef) extends Actor with Routing{
  import MeanCalculator._
  val meanCalcRouter = createRouter(context, 4, MeanCalculator.props)
  val fillMeanRouter = createRouter(context, 4, PlayerMeanFiller.props)
  var valueCount = 0
  //use tree map to keep it sorted
  var meanVals = TreeMap.empty[Int, Int]
  var playerList = List.empty[Player]
  var finalPlayerList = List.empty[FinalPlayer]
  var meanValPlayer: ValueVector = Vector.empty[Int]
  var weightVector = Vector.empty[Int]

  def receive = start

  //receives the player list and distributes the player values to the MeanCalculator
  def start: Receive = {
    case PlayerListWithWeightVector(pls, w) =>
      weightVector = w

      playerList = pls.players
      val listOfAllValues: List[ValueVector] = pls.players.map(_.values)
      valueCount = listOfAllValues.head.size
      for (i <- 0 until valueCount) {
        meanCalcRouter.route(ListToCalculate(i,listOfAllValues), self)
      }
      context become optimizing
  }

  def optimizing: Receive = {
    case Mean(identifier, mean) => meanVals = meanVals + (identifier -> mean)
      if (meanVals.size == valueCount) {
        meanValPlayer = meanVals.values.toVector
        playerList foreach(p => fillMeanRouter.route(FillMeanVals(MeanVals(meanValPlayer), p, weightVector),self))
      }
    case FinalPlayerMessage(player) => finalPlayerList = player :: finalPlayerList
      if(finalPlayerList.size == playerList.size) master ! FinalPlayers(finalPlayerList, meanValPlayer :+ meanStrength(finalPlayerList))
  }

  def meanStrength(finalPlayerList: List[FinalPlayer]): Strength = {
    finalPlayerList.map(_.finalStrength).sum / finalPlayerList.size
  }
}