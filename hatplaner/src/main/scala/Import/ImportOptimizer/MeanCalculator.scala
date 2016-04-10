package Import.ImportOptimizer

import Import.Types.ValueVector
import akka.actor.{Actor, Props}

/**
  * Created by yannick on 14.02.16.
  */
object MeanCalculator {
  val props = Props(new MeanCalculator)
  case class ListToCalculate(position: Int,values: List[ValueVector])
  case class Mean(position: Int, mean: Int)
}

class MeanCalculator extends Actor {
  import MeanCalculator._

  def receive: Receive = {
    case ListToCalculate(identifier, values) => sender ! calculateMean(identifier,values)
  }

  def calculateMean(position: Int, values: List[ValueVector]): Mean = {
    val getColumn: (ValueVector => Int) = vector => vector(position)

    val columnWithoutNulls = values map getColumn filter(_ != 0)

    Mean(position , if(columnWithoutNulls.nonEmpty) columnWithoutNulls.sum * 100 / columnWithoutNulls.length else 0)
  }
}