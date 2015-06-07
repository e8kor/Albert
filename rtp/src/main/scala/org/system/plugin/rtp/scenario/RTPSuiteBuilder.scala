package org
package system.plugin.rtp
package scenario

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory.parseFile
import com.typesafe.scalalogging.LazyLogging
import info.folone.scala.poi._

import scala.language.{higherKinds, postfixOps}
import scala.math.Ordering
import scala.reflect.io.Directory

/**
 * Created by evgeniikorniichuk on 20/04/15.
 */
object RTPSuiteBuilder {

  sealed case class AbsentSheet(name: String) {
    override def toString = {
      freeText("sheetEmpty") format name
    }
  }

  trait RTPSheet {

    import scalaz._
    import Scalaz._

    def name: String

    def validate(book: Workbook): AbsentSheet \/ Sheet = {
      for {
        sheet <- ((book sheetMap) get name) \/> AbsentSheet(name)
      } yield sheet
    }
  }

  object TransportSheet extends RTPSheet {
    import scalaz.\/

    val name = "Transport"

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

    def getValuesForStringCells(seq: Seq[StringCell]): Seq[String] = {
      seq map (cell => cell data)
    }

    def stringCellsFromRow(seq: Seq[Cell]) = {
      for {
        cell <- seq if (cell isInstanceOf)[StringCell]
      } yield (cell asInstanceOf)[StringCell]
    }

    def apply(book: Workbook): AbsentSheet \/ Seq[Seq[String]] = {
      val rows = validate(book) map {
        sheet =>
          set2SortedSeq(sheet rows)
      } map {
        rows =>
          rows map {
            row =>
              getValuesForStringCells(
                stringCellsFromRow(
                  set2SortedSeq(row cells)
                )
              )
          }
      }
      rows
    }

  }


  object SetupSheet extends RTPSheet {
    val name = "Setup"
  }

  object DataSheet extends RTPSheet {
    val name = "Data"
  }

  object TemplateSheet extends RTPSheet {
    val name = "Template"
  }

  object ScenarioSheet extends RTPSheet {
    val name = "Scenario"
  }

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