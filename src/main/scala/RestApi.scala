import BoxOffice._
import akka.actor.ActorRef
import akka.util.Timeout
import akka.pattern.{ask, pipe}

import scala.concurrent.ExecutionContext

class RestApi {

}

trait BoxOfficeApi{

  def createBoxOffice() : ActorRef

  implicit def executionContext : ExecutionContext

  implicit def requestTimeout : Timeout

  lazy val boxOffice = createBoxOffice()

  def createEvent(event : String,nrOfTickets : Int) = {
    //(boxOffice ? CreateEvent(event,nrOfTickets)).mapTo[EventResponse]
    (boxOffice ? CreateEvent(event,nrOfTickets)).mapTo[EventResponse]
  }

  def getEvents() = {
    (boxOffice ? GetEvents ).mapTo[EventResponse]
  }

  def getEvent(event : String) = {
    (boxOffice ? GetEvent(event)).mapTo[Option[Event]]
  }






}
