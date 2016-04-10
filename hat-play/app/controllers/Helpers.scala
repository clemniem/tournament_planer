package controllers

import java.io.File

import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData.FilePart

import scala.io.Source
import scala.util.Try

trait Helpers {
  def formatTeams(teams: List[String]): String = {
    teams.map{
      case ":" => "</div>"
      case ";" => "<div class=\"single-team\">"
      case team:String if team.contains("Team") => s"<h2>$team</h2>"
      case any => s"<p>$any</p>"
    }.reduce(_ + _)
  }

  def formatTournament(tournament: List[String]): String = {
    val x = tournament.map(_.split(",,").map("<div>" + _ + "</div>").toList)
    val res = for (i <- x.head.indices) yield x.map(a=>Try(a(i)))
    res.map(outer => "<div class=\"column\">"+outer.map(inner => inner.getOrElse("")).reduce(_+_)+"</div>")
      .reduce(_+_)
      .replaceAll("""\(.*?\)""","")
      .replaceAll("""[0-9][0-9]?,([a-z,A-Z])""","$1")
      .replaceAll(",","")
  }

  def convertFileToList(inputCsv: FilePart[TemporaryFile]): List[String] = {
    Source.fromFile(inputCsv.ref.file)("UTF-8").getLines().drop(1).toList
  }
}
