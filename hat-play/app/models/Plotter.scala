package models

import akka.actor.{Actor, Props}
import models.Plotter.{PlotData, ChartPath}

import scalax.chart.api._

/**
  * mini actor to plot data
  */
object Plotter{
  val props = Props(new Plotter)
  val name = "plotter"

  case class PlotData(data: Seq[(Int,Int)])
  case class ChartPath(name: String)
}

class Plotter extends Actor{
  def receive: Receive = {
    case PlotData(data) =>
      implicit val theme = org.jfree.chart.StandardChartTheme.createLegacyTheme()
      val chart = XYLineChart(data,s"count of the ${data.map(_._2).sum} evals")
      val chartPath = s"/tmp/chart"+scala.math.random+".png"
      chart.saveAsPNG(chartPath)
      sender ! ChartPath(chartPath)
  }
}
