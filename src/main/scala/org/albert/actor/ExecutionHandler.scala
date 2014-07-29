import akka.actor.Props
import org.albert.actor._

/**
 * Created by nutscracker on 6/30/2014.
 */
object ExecutionHandler {

  def apply() = Props(classOf[ExecutionHandler])

}

class ExecutionHandler extends RTProcessor {

  val runner = context.actorOf(Runner())

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
