package Import

import akka.actor.{Props, ActorContext}
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}

/**
  * Created by yannick on 14.02.16.
  */
trait Routing {
  def createRouter(context: ActorContext, actorCount: Int, props: Props) = {
    val routees = Vector.fill(actorCount) {
      val r = context.actorOf(props)
      //context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }
}
