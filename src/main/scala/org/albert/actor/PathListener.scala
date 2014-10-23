package org.albert
package actor


import akka.actor.ActorRef

/**
 * Created by nutscracker on 6/30/2014.
 */

object PathListener extends ActorObject

class PathListener extends AlbertActor {

  import com.beachape.filemanagement.Messages.RegisterCallback
  import scala.reflect.io.Path
  import com.beachape.filemanagement.MonitorActor
  import org.albert.command.SuitePath
  import scala.reflect.io.Directory
  import java.nio.file.StandardWatchEventKinds._

  val fileMonitorActor = (context system) actorOf MonitorActor(concurrency = 2)

  override def receive: Receive = {
    case path:Path => sender ! registerCallback(requiredFiles(path))
  }

  def wihFileCallback(path:Path) = {
    import java.nio.file.StandardWatchEventKinds._

    path ifFile{
      file =>
        Seq(RegisterCallback(ENTRY_MODIFY, None, false, file, {v => log info s"updated file by path $v"}))
    } getOrElse Seq[RegisterCallback]()
  }
  
  def withDirCallback(path:Path) = {
    val normalized = path ifFile {file => file parent} getOrElse (path toDirectory)
    normalized ifDirectory {
      dir =>
        def forDir(path:Path):Unit ={
          log info s"created file by path $path"
          (context parent) ! ??? // TODO: Handle SuiteOrder update
        }
        Some(
          RegisterCallback(ENTRY_CREATE, None, false, dir, {
            v =>
              log info s"created file by path $v"
          })
        )
    }
  }

  def registerCallback(dirs:Iterator[Path]): Unit = {
    dirs map {path => wihFileCallback(path) ++ withDirCallback(path)} reduce ( _ ++ _ ) foreach( fileMonitorActor ! _)
  }

  def requiredFiles(path: Path) = path walkFilter (entry => entry isFile /* && TODO with list of required files */)

  private def initiateSubSuite(path:Path) = {

    def createSuitesByPath(path:Path) = {
      def filter(dir:Directory):Option[Path] = {
        ((dir files) toSet) find ( file => (file name) equals suiteFileName)
      }

      def initiateActorsForSubDirs(it:Directory) = {
        it checkSubSuiteDirs filter map {
          subSuite =>
            val slave = (context system) actorOf (SuiteManager(), subSuite path)
            slave ! SuitePath(subSuite)
            slave
        }
      }

      path ifDirectory initiateActorsForSubDirs getOrElse Set[ActorRef]()
    }

    val slaves = createSuitesByPath(path)
    slaves
  }


}
