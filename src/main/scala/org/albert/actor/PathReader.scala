package org.albert.actor

import java.io.{File, FileFilter}
import java.nio.file.Path

import akka.actor.Props
import com.beachape.filemanagement.Messages.RegisterCallback

/**
 * Created by nutscracker on 6/30/2014.
 */

object PathReader {

  def apply() = Props(classOf[PathReader])

}

class PathReader extends RTActor {

  import org.albert.{defaultCallback => callbacks}

  implicit val dirFilter = new FileFilter {
    override def accept(file: File): Boolean = file.isDirectory
  }

  override def receive: Receive = {

    case path:Path => sender ! withCallbacks(directories(path))

    case default => log.warning(s"cant process message $default")

  }

  def withCallbacks(dirs:Seq[File]) = {
    (Map[Path, Seq[RegisterCallback]]() /: dirs) {
      case (accum, file) =>
        accum + (file.toPath -> callbacks(file.toPath))
    }
  }

  def directories(path: Path):Seq[File] = {
    ((path toFile) listFiles dirFilter) toSeq
  }


}
