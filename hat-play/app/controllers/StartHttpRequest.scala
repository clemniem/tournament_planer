package controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.OutgoingConnection
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Flow}
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future

/**
  * starts a new future http request with akka-http
  */
trait StartHttpRequest {
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer

  def httpRequest(data: String, path: String, targetPort: Int, host: String): Future[HttpResponse] = {
    val connectionFlow: Flow[HttpRequest, HttpResponse, Future[OutgoingConnection]] = {
      Http().outgoingConnection(host, port = targetPort)
    }
    val responseFuture: Future[HttpResponse] =
      akka.stream.scaladsl.Source.single(RequestBuilding.Post(uri = path, entity = HttpEntity(ContentTypes.`application/json`, data)))
        .via(connectionFlow)
        .runWith(Sink.head)
    responseFuture
  }
}

