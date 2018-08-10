import BoxOffice.Event
import akka.actor.{Actor, PoisonPill, Props}

class TicketSeller(event : String) extends Actor{

  import TicketSeller._

  var tickets = Vector.empty[Ticket]
  def receive = {

    case Add(newTickets) => tickets = tickets ++ newTickets
    case Buy(nrTickets) => {
      //here we take tickets out of our ticket vector, if there are enough, we respond with the event and tickets
      //if the tickets are not enough we juts respond with a empty tickets
      val ticks = tickets.take(nrTickets).toVector
      if(nrTickets <= ticks.size) {
        sender() ! Tickets(event,ticks)
        tickets.drop(nrTickets)
      }else sender() ! Tickets(event)
    }
    case GetEvent => sender() ! Some(Event(event,tickets.size))
    case Cancel => {
      sender() ! Some(Event(event,tickets.size))
      self ! PoisonPill
    }

  }




}

object TicketSeller{

  def props(event: String) = Props(new TicketSeller(event))

  case class Add(tickets: Vector[Ticket]) //message received to add tickets
  case class Buy(tickets: Int) //message received to buy tickets
  case class Ticket(id: Int) //a ticket
  case class Tickets(event: String, entries: Vector[Ticket] = Vector.empty[Ticket]) //message to get all tickets
  case object GetEvent //message to get event
  case object Cancel

}
