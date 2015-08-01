
import scala.sys.process._
val prc = "ls" #| "grep .scala" #&& Seq("sh", "-c", "scalac *.scala") #|| "echo nothing found"

prc.lineStream