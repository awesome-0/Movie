//import java.util.concurrent.TimeUnit
//
//import akka.actor.{Actor, ActorSystem, Props}
//import akka.pattern.ask
//import akka.util.Timeout
//import scala.concurrent.ExecutionContext.Implicits.global
//
//import scala.util.{Failure, Success}
//
//
//object testing extends App{
//  case object AskedMessage {
//    def apply: Any = ???
//  }
//
//  class Askee extends Actor{
//
//    def receive = {
//      case AskedMessage => sender() ! "you asked for my age, it is 54"
//    }
//
//  }
//  class Asker extends Actor{
//    def receive = {
//      case _ => println("")
//    }
//  }
//
//  val system  = ActorSystem("Ask")
//  val asker = system.actorOf(Props[Asker],"asker")
//  val askee = system.actorOf(Props[Askee],"askee")
//  implicit val timeout = Timeout(5.toLong,TimeUnit.SECONDS)
//
//  val result = askee ? AskedMessage
//
//  result.onComplete{
//    case Success(e) => println(e)
//    case Failure(e) => println(e)
//  }
//
//}
