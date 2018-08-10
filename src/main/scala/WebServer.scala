import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import scala.io.StdIn

object WebServer extends Directives{
  case class Order(num : Int)




  def main(args: Array[String]) {
    // needed to run the route
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    // needed for the future map/flatmap in the end and future in fetchItem and saveOrder
    implicit val executionContext = system.dispatcher
    implicit val orderFormat = jsonFormat1(Order)

    val route: Route =
        post {
          path("create") {
            entity(as[Order]) { order =>
              complete(HttpResponse(StatusCodes.OK,entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`,s"the received case class is ${order.num}")))
            }
            }
          }


    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ ⇒ system.terminate()) // and shutdown when done

  }

}
