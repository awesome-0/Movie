import BoxOffice._
import TicketSeller.{Ticket, Tickets}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import akka.pattern.{ask, pipe}
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.marshalling._
import spray.json.RootJsonFormat
//import spray.json.DefaultJsonProtocol
import spray.json.DefaultJsonProtocol._


import scala.concurrent.ExecutionContext

class RestApi (system: ActorSystem, timeout: Timeout) extends RestRoutes {


   implicit def executionContext: ExecutionContext = system.dispatcher

   implicit def requestTimeout: Timeout = timeout
  override def createBoxOffice(): ActorRef = system.actorOf(BoxOffice.props,BoxOffice.name)
}


trait RestRoutes extends BoxOfficeApi
  with EventMarshalling{
  import StatusCodes._

  def routes: Route = eventsRoute ~ addEvent ~ ticketsRoute


  def eventsRoute = get {
      path("events") {
      onSuccess(getEvents()){
        case events : Events => complete(events)
        case _ => complete(NotFound)
      }
    }
  }



 def addEvent =
   path("events" / Segment){
     event => {
       post {
         entity(as[EventDescription])  {
           ed => {
             onSuccess(createEvent(event,ed.tickets)) {
               case EventCreated(e) => complete(OK,e)
               case EventExists =>
                 val err = Error(s"$event event already exists")
                 complete(BadRequest,err)
             }
           }
         }
     } ~
       get {
         onSuccess(getEvent(event)) {
           case Some(evnt) => complete(OK,evnt)
           case None => complete("hello")
         }
      } ~
       delete {
         onSuccess(cancelEvent(event)) {
           case Some(evnt) => complete(OK,evnt)
           case None => complete("hello")
         }
       }
     }
 }

  def ticketsRoute = path("events" / Segment / "tickets"){
    event => {
      post{
        entity(as[TicketRequest]){
          tR => {
            onSuccess(requestTickets(event,tR.tickets)) {
              ticks => {
                if(ticks.entries.isEmpty) complete(NotFound) else complete(OK,ticks)
              }
//             ticks => {
//               if(ticks.get.entries.isEmpty) complete
//              case ticks => {
//                if(ticks.entries.isEmpty) complete(NotFound) else (complete(OK,Ticket(ticks.entries.size)))
             }
            }
          }
        }
      }
    }



  /*


  def eventRoute =
    pathPrefix("events" / Segment) { event =>
      pathEndOrSingleSlash {
        post {
          // POST /events/:event
          entity(as[EventDescription]) { ed =>
            onSuccess(createEvent(event, ed.tickets)) {
              case BoxOffice.EventCreated(event) => complete(Created, event)
              case BoxOffice.EventExists =>
                val err = Error(s"$event event exists already.")
                complete(BadRequest, err)
            }
          }
        } ~
        get {
          // GET /events/:event
          onSuccess(getEvent(event)) {
            _.fold(complete(NotFound))(e => complete(OK, e))
          }
        } ~
        delete {
          // DELETE /events/:event
          onSuccess(cancelEvent(event)) {
            _.fold(complete(NotFound))(e => complete(OK, e))
          }
        }
      }
    }



  def ticketsRoute =
    pathPrefix("events" / Segment / "tickets") { event =>
      post {
        pathEndOrSingleSlash {
          // POST /events/:event/tickets
          entity(as[TicketRequest]) { request =>
            onSuccess(requestTickets(event, request.tickets)) { tickets =>
              if(tickets.entries.isEmpty) complete(NotFound)
              else complete(Created, tickets)
            }
          }
        }
      }
    }
   */


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
    (boxOffice ? GetEvents ).mapTo[Events]
  }

  def getEvent(event : String) = {
    (boxOffice ? GetEvent(event)).mapTo[Option[Event]]
  }

  def cancelEvent(event : String) = {
    (boxOffice ? CancelEvent(event)).mapTo[Option[Event]]
  }

  def requestTickets(event : String, nrTickets : Int) = {
    (boxOffice ? GetTicketsforEvent(event,nrTickets)).mapTo[TicketSeller.Tickets]
  }






}
