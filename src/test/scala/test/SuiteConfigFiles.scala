package test

import scala.language.postfixOps
import scala.reflect.io.Directory

object SuiteConfigFiles {

  val relativePath ={
    val url = getClass getResource "/TestSuites"

    url getFile
  }
  val testSuite1Dir = Directory(s"$relativePath/TestSuite1")

  val testSuite2Dir = Directory(s"$relativePath/TestSuite2")

  val testSuite3Dir = Directory(s"$relativePath/TestSuite3")

  val testSuite4Dir = Directory(s"$relativePath/TestSuite4")

  val testSuite5Dir = Directory(s"$relativePath/TestSuite5")

  val testSuite6Dir = Directory(s"$relativePath/TestSuite6")

  val testSuite7Dir = Directory(s"$relativePath/TestSuite7")

}
