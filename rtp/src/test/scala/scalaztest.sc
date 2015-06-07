import scala.reflect.io.File
import scalaz._
import scalaz.concurrent.Task

val task = Task{println("hello World")}


import scalaz.stream._

val file = File("/Users/evgeniikorniichuk/Documents/Workspace/System/rtp-plugin/src/test/resources/Matcher/Full/test_DEV2.properties")
//val process = io.(file.inputStream()).map