package org
package system.plugin.rtp
package scenario

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory.parseFile
import com.typesafe.scalalogging.LazyLogging
import info.folone.scala.poi._

import scala.language.{higherKinds, postfixOps}
import scala.reflect.io.Directory

object RTPSuiteBuilder {

  sealed trait RTPSheet {

    def name: String

  }

  sealed case class AbsentSheet(name: String) extends RTPSheet {

    override def toString = {
      freeText("sheetEmpty") format name
    }

  }



  object TransportSheet extends RTPSheet {

    import scalaz._

    val name = "Transport"

    def validate(book: Workbook): AbsentSheet \/ Sheet = {
      import Scalaz._
      for {
        sheet <- ((book sheetMap) get name) \/> AbsentSheet(name)
      } yield sheet
    }

    def cellValue(cell: Cell): Any = {
      cell match {
        case NumericCell(index, data) => data
        case BooleanCell(index, data) => data
        case StringCell(index, data) => data
        case FormulaCell(index, data) => data
        case StyledCell(nestedCell, style) => cellValue(nestedCell)
      }
    }

    def set2SortedSeq[A: Ordering](cells: Set[A]): Seq[A] = {
      (cells toList) sorted
    }

    def valuesFromStringCells(seq: Seq[StringCell]): Seq[String] = {
      seq map stringCell2Value
    }

    def stringCell2Value(cell: StringCell): String = {
      cell data
    }

    def stringCellsFromRow(seq: Seq[Cell]) = {
      for {
        cell <- seq if (cell isInstanceOf)[StringCell]
      } yield (cell asInstanceOf)[StringCell]
    }

    def asStringCell(cell: Cell) = {
       (cell asInstanceOf)[StringCell]
    }

    def isStringCell(cell:Cell) = {
      (cell isInstanceOf)[StringCell]
    }

    def apply(book: Workbook): AbsentSheet \/ Seq[Seq[String]] = {
      val a:AbsentSheet \/ Seq[Seq[String]] = for {
        sheet <- validate(book)
        row <- set2SortedSeq(sheet rows)
        cell <- set2SortedSeq(row cells) if isStringCell(cell)
      } yield stringCell2Value(asStringCell(cell))

      a
    }

  }


//  object SetupSheet extends RTPSheet {
//    val name = "Setup"
//  }
//
//  object DataSheet extends RTPSheet {
//    val name = "Data"
//  }
//
//  object TemplateSheet extends RTPSheet {
//    val name = "Template"
//  }
//
//  object ScenarioSheet extends RTPSheet {
//    val name = "Scenario"
//  }

}

case class RTPSuiteBuilder(config: Config, dir: Directory) extends LazyLogging {

  val propsPath = dir / (config getString "propsFile")
  val suiteProps = parseFile(propsPath jfile) withFallback config getConfig (dir name)
  val suiteScenario = dir / (config getString "propsFile") toFile


  Workbook(suiteScenario inputStream()) fold(
    exception =>
      logger error("Unable to open xls file", exception),
    workbook => {
      logger info("Workbook parsed successfully", workbook)

    })

}