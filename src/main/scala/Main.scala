import java.util.concurrent.TimeUnit

import BoxOffice.Events
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import spray.json.DefaultJsonProtocol._


import scala.concurrent.ExecutionContext.Implicits.global

object Main {


  def main(args: Array[String]): Unit = {


    implicit val system = ActorSystem("tickets")
    implicit val materializer = ActorMaterializer()
    implicit  val timeout = Timeout(5.toLong,TimeUnit.SECONDS)
    //implicit val eventMarshall= jsonFormat1(BoxOffice.Events)

    val route = new RestApi(system,timeout).routes

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    scala.io.StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ ⇒ system.terminate()) // and
  }

}
