package models

import java.io.{BufferedWriter, File, FileWriter}

import akka.actor.{Actor, Props}
import models.CsvFileWriter.OutputPath

object CsvFileWriter{
  val name = "file-writer"
  val props = Props(new CsvFileWriter)
  case class OutputPath(path: String, lines: List[String])
}

/**
  * mini actor to save to a csv
  */
class CsvFileWriter extends Actor{

  //receives OutputPath and writes file, sends OutputPath back to master after writing file
  def receive: Receive = {
    case OutputPath(path, lines) =>
      writeFile(path,lines)
      sender ! OutputPath(path, lines)
  }


  //takes a list of lines and writes them to path
  def writeFile(path: String, lines: List[String]) = {
    val bw = new BufferedWriter(new FileWriter(new File(path)))
    for (line <- lines) bw.write(line+"\n")
    bw.close()
  }
}
