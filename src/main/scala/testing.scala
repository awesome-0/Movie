import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global

import scala.util.{Failure, Success}


object testing extends App{
  case object AskedMessage {
    def apply: Any = ???
  }

  class Askee extends Actor{
    val asker = context.actorOf(Props[Asker],"asker")

    def receive = {
      case msg : String => asker forward(msg)
      case AskedMessage => sender() ! "hello"
    }

  }
  class Asker extends Actor{
    def receive = {
      case msg : String => println(s"message received is $msg and its sender is ${sender.path} ")
    }
  }
  class Askeree extends Actor{
    val asker = context.actorOf(Props[Askee],"askee")
    def receive = {
      case msg : String => {
        asker ! msg
      }
    }
  }

  val system  = ActorSystem("Ask")
 // val asker = system.actorOf(Props[Asker],"asker")
  val askee = system.actorOf(Props[Askee],"asker")

  implicit val timeout = Timeout(5.toLong,TimeUnit.SECONDS)

  val result = askee ? AskedMessage

  result.onComplete{
    case Success(e) => println(e)
    case Failure(e) => println(e)
  }

  //askee ! "hello"

}
