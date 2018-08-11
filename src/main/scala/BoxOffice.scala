import java.util.concurrent.TimeUnit


import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class BoxOffice extends Actor{
  import BoxOffice._
  import akka.pattern.{ask,pipe}
  import akka.util.Timeout
  implicit val timeout = Timeout(5.toLong,TimeUnit.SECONDS)

  def createTicketSeller(event : String) = context.actorOf(TicketSeller.props(event))

  def receive = {
    case CreateEvent(name,ticks) => {
      def create() = {
        //let us first create a ticket seller

        val ticketSeller = createTicketSeller(name)
        val tickets = (1 to ticks).map{
          ticketId => TicketSeller.Ticket(ticketId)
        }.toVector

        ticketSeller ! TicketSeller.Add(tickets)
        sender() ! EventCreated
      }
      /*

            So the basic pattern (and my proposed formatting standard) for folding over
      an `Option[A]` from which you need to produce a B (which may be Unit if
      you're only interested in side effects) is:

      anOption.fold
        {
          // something that evaluates to a B if anOption = None
        }
        { a =>
          // something that transforms `a` into a B if anOption = Some(a)
  }
       */

      context.child(name).fold(create()){_ => sender() ! EventExists}
    }

    case GetTicketsforEvent(event,tickets) => {
      def notFound() = sender() ! TicketSeller.Tickets(event)
      def buy(child : ActorRef) = {
        child.forward(TicketSeller.Buy(tickets)) // we use the forward so the response will go to parent, so response will go to  rest API
      }
      context.child(event).fold(notFound())(buy)
    }
    case GetEvents => {
      def getEvents = context.children.map{
        ticketseller => {
          (self ? GetEvent(ticketseller.path.name)).mapTo[Option[Event]]
        }
      }

      def convertToEvents(future : Future[Iterable[Option[Event]]]) = {
        future.map(_.flatten).map{ event => Events(event.toVector)}
      }
      pipe(convertToEvents(Future.sequence(getEvents))).to(sender())
    }

    case GetEvent(eventname) => {
      def notFound = sender() ! None
      def getEvent(child : ActorRef) = child forward TicketSeller.GetEvent
      context.child(eventname).fold(notFound)(getEvent)
    }

    case CancelEvent(event) => {
      def notFound() = sender() ! None
      def cancelEvent(child : ActorRef) = child forward TicketSeller.Cancel
    }
  }



}

object BoxOffice{

  def props(implicit timeout : Timeout) = Props(new BoxOffice)
  def name = "BoxOffice"
  case class CreateEvent(name: String, tickets: Int) //message to create an event
  case class GetEvent(name: String) //message to get an event
  case object GetEvents //message to get all events
  case class GetTicketsforEvent(event: String, tickets: Int) //message to buy tickets for an event
  case class CancelEvent(name: String) //message sent by box office to cancel an event

  case class Event(name: String, tickets: Int) ///message describing an event
  case class Events(events: Vector[Event]) //message listing all the events

  sealed trait EventResponse //message response to an event either its created or it exists
  case class EventCreated(event: Event) extends EventResponse
  case object EventExists extends EventResponse

}
