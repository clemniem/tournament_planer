package Import.FileImport

import Import.{Caster, Player}
import akka.actor.{Actor, Props}

/**
  * The line importer actor reads a Line and converts it to the Player Datatype
  */
object CsvLineImporter {
  val props = Props(new CsvLineImporter)
  case class InputLine(line: String)
  case class ErrorInLine(message: String)
}
class CsvLineImporter extends Actor with ConvertToInt{
  import CsvLineImporter._

  //converts the input line to a Player class
  def parsedPlayer(line: String): Player = {
    import Import.Caster._
    (line + ",eol").split(",") match {
      case Array(name,team,gender,exp,agi,fit,han,rec,posWish,negWish, eol) if !name.isEmpty =>
        Player(name,team, toGender(gender),
          Vector(
            convertToInt(exp),
            convertToInt(agi),
            convertToInt(fit),
            convertToInt(han),
            convertToInt(rec)),
          posWish,negWish)
    }
  }

  def receive: Receive = {
    case InputLine(line) => sender ! parsedPlayer(line)
  }

  //is used to pass the last message to the supervisor in case of exception
  @throws[Exception](classOf[Exception])
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    sender ! ErrorInLine(message.get match{
      case i: InputLine => i.line.toString
      case any => any.toString
    })
    super.preRestart(reason, message)
  }
}

trait ConvertToInt {
  def convertToInt(s: String): Int = if (s.isEmpty) 0 else s.toInt
}