package controllers

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.util.Timeout
import models.CsvFileWriter.OutputPath
import models.{CsvFileWriter, Plotter}
import models.Plotter.{ChartPath, PlotData}
import play.api.libs.Files.TemporaryFile
import play.api.mvc._
import spray.json._
import akka.pattern.ask
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * InputController manges incomint Http requests
  */
class InputController extends Controller with Protocols with StartHttpRequest with Helpers with InputForms{
  implicit val timeout = Timeout(30.seconds)
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val plotter = system.actorOf(Plotter.props)
  val csvWriter = system.actorOf(CsvFileWriter.props)

  //index page route
  def index = Action {
    Ok(views.html.index())
  }

  //download the file from the tmp directory
  def download(fileName: String) = Action {
    if (fileName.contains('/')) BadRequest("No permission")
    else {
      val file = new File(s"/tmp/$fileName")
      Ok.sendFile(
        content = file,
        fileName = _ => fileName
      )
    }
  }

  //post to teams only route
  def getTeams = Action.async(parse.multipartFormData) {
    implicit request =>
      //try to bind the form request
      teamsForm.bindFromRequest.fold(
        formWithErrors => {
          //failure during binding
          //show error message
          Future(BadRequest(formWithErrors.errors.seq
            .map(formE => "Error with: " + formE.key + " because of: " + formE.messages.reduce(_ + _)).reduce(_ + _)))
        },
        //binding success
        formInput => {
          createFutureResultResponse(request, formInput, createFinishedTeamsResponse)
        }
      )
  }

  //post to teams and tournament route
  def getTeamsAndTournament = Action.async(parse.multipartFormData){
    implicit request =>
      teamTournamentForm.bindFromRequest.fold(
        formWithErrors => {
          //failure during binding
          Future(BadRequest(formWithErrors.errors.seq
            .map(formE => "Error with: " + formE.key + " because of: " + formE.messages.reduce(_ + _)).reduce(_ + _)))
        },
        formInput => {
          createFutureResultResponse(request, formInput, getTournamentAndCreateResponse)
        }
      )
  }

  //post to  only tournament route
  def tournament = Action.async(parse.multipartFormData) {
    implicit request =>
      tournamentForm.bindFromRequest.fold(
        formWithErrors => {
          //failure during binding
          Future(BadRequest(formWithErrors.errors.seq
            .map(formE => "Error with: " + formE.key + " because of: " + formE.messages.reduce(_ + _)).reduce(_ + _)))
        },
        formInput => {
          request.body.file("input").map { inputCsv =>
            //drops first line
            val inputList: List[String] = convertFileToList(inputCsv)
            val teamCsv = csvWriter.ask(OutputPath(s"/tmp/teams${scala.math.random}.csv",inputList)).mapTo[OutputPath].flatMap(out => Future(out.path))
            getTournamentAndCreateResponse(pathCsvFut = teamCsv, teamsList = inputList, form = formInput)
          }
        }.getOrElse(Future(play.api.mvc.Results.BadRequest))
      )
  }

  //creates a Response which redirects to the upload side
  def createFinishedTeamsResponse(pathPlotFut: Future[String], pathCsvFut: Future[String], teamsList: List[String], form: InputForm): Future[Result] = {
    for {
      chartPath <- pathPlotFut
      csvPath <- pathCsvFut
    } yield Ok(views.html.team(formatTeams(teamsList))("download?file=" + chartPath.split("/").last)("download?file=" + csvPath.split("/").last))
  }

  //creates an additional http request to the tournament service
  //and then returns a result
  def getTournamentAndCreateResponse(pathPlotFut: Future[String] = Future(""), pathCsvFut: Future[String] = Future(""), teamsList: List[String], form: InputForm): Future[Result] = {
    val tf = form.asInstanceOf[TournamentRequirements]
    val getTeamTournament = TournamentRequest(teamsList, tf.startTime, tf.endTime, tf.gameTime, tf.pauseTime, tf.fields, tf.gameMode, tf.days)
    val responseFromTournament: Future[HttpResponse] = httpRequest(getTeamTournament.toJson.toString, "/getTournament", system.settings.config.getInt("akka.tournament.port"), system.settings.config.getString("akka.tournament.host"))
    responseFromTournament.flatMap { tournamentResponse =>
      tournamentResponse.status match {
        case StatusCodes.OK =>
          val resultTournament = Unmarshal(tournamentResponse.entity).to[FinishedSchedule].flatMap { finishedSchedule =>
            val pathTournamentFut = csvWriter.ask(OutputPath(s"/tmp/tournament${scala.math.random}.csv",finishedSchedule.slots)).mapTo[OutputPath].flatMap(out => Future(out.path))
            for {
              pathTournament <- pathTournamentFut
              pathPlot <- pathPlotFut
              csvPath <- pathCsvFut
            } yield Ok(views.html.tournamentAndTeam(formatTeams(teamsList))(formatTournament(finishedSchedule.slots))("download?file=" + pathPlot.split("/").last)("download?file=" + csvPath.split("/").last)("download?file=" + pathTournament.split("/").last))
          }
          resultTournament
        case StatusCodes.BadRequest =>
          Unmarshal(tournamentResponse.entity).to[String].map{str => BadRequest(str)}
        case any => Future(BadRequest)
      }
    }
  }

  //reads in the file and the form data and
  //creates an http request to the team building service and then initiates creation
  //of the output png
  //additional takes a method to create the result
  def createFutureResultResponse(request: Request[MultipartFormData[TemporaryFile]], formInput: InputForm, moreAction: (Future[String], Future[String], List[String], InputForm) => Future[Result]): Future[Result] = {
    request.body.file("input").map { inputCsv =>
      val inputList: List[String] = convertFileToList(inputCsv)
      val teamRequest = TeamRequest(inputList, formInput.teams, formInput.weightVector)
      //http request for teams
      val responseFuture: Future[HttpResponse] = httpRequest(teamRequest.toJson.toString, "/getTeams", system.settings.config.getInt("akka.team.port"), system.settings.config.getString("akka.team.host"))
      //parse the result of the request to a readable format
      val formattedResult = responseFuture.flatMap { response =>
        response.status match {
          case StatusCodes.OK =>
            //get the result object from the httpresponse
            val resultTeamsAndEval = Unmarshal(response.entity).to[ResultTeamsAndEval]
            val futureResult = resultTeamsAndEval.map { result => {
              //the result teams
              val teamList = result.teams
              //get the path to the png from the local actor
              val pathPlotFut = plotter.ask(PlotData(result.eval)).mapTo[ChartPath].flatMap(p => Future(p.name))
              //wirte the file to tmp
              val pathCsvFut = csvWriter.ask(OutputPath(s"/tmp/teams${scala.math.random}.csv",teamList)).mapTo[OutputPath].flatMap(out => Future(out.path))
              //create the response
              moreAction(pathPlotFut,pathCsvFut, teamList, formInput)
            }
            }.flatMap(a => a)
            futureResult
          case StatusCodes.BadRequest =>
            //display the bad lines
            Unmarshal(response.entity).to[BrokenLines].map(a => Ok("Please check the following lines for error: \n\n" + a.broken.reduce(_ + "\n" + _)))
        }
      }
      formattedResult
      //if something went wrong
    }.getOrElse(Future(play.api.mvc.Results.BadRequest))
  }
}



