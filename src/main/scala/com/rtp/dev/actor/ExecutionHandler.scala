import akka.actor.Actor.Receive
import akka.actor.Props
import com.beachape.filemanagement.Messages.RegisterCallback
import com.beachape.filemanagement.MonitorActor
import com.rtp.dev.actor._
import com.rtp.dev.{Restart, Start, SuitePath}

/**
 * Created by nutscracker on 6/30/2014.
 */
object ExecutionHandler {

  def apply() = Props(classOf[ExecutionHandler])

}

class ExecutionHandler extends RTProcessor {

  val runner = context.actorOf(Runner)

  def normal: Receive = {
    case ExecutionScope(tasks, bundle) =>
      context.become(inProgress)
      implicit val implicitBundle = bundle
      runner ! (tasks map prepareTask)
  }

  def inProgress: Receive = {
    case ExecutionScope(tasks,bundle) =>
  }

  override def receive = normal

  def prepareTask(task:Task)(implicit bundle:Bundle):Task = ???

}
