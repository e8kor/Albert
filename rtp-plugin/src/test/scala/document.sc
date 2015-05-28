import com.typesafe.config.ConfigFactory
import info.folone.scala.poi._
import org.apache.poi.ss.usermodel.Cell.{CELL_TYPE_BLANK, CELL_TYPE_BOOLEAN, CELL_TYPE_ERROR, CELL_TYPE_FORMULA, CELL_TYPE_NUMERIC, CELL_TYPE_STRING}

import scala.collection.JavaConversions._
import scala.language.postfixOps
import scala.reflect.io.File
import scala.reflect.io.Path._
import scalaz.Show

implicit object RowOrdering extends Ordering[Row] {
  override def compare(x: Row, y: Row): Int = {
    (x index) compareTo (y index)
  }
}

implicit object CellOrdering extends Ordering[Cell] {
  override def compare(x: Cell, y: Cell): Int = {
    (x index) compareTo (y index)
  }
}

implicit object ShowData extends Show[Cell]

def cellValue(cell:Cell):Any = {
  cell match {
    case NumericCell(index, data) => data
    case BooleanCell(index, data) => data
    case StringCell(index, data) => data
    case FormulaCell(index,data) => data
    case StyledCell(nestedCell, style) => cellValue(nestedCell)
  }
}

val transport = "Transport"

val document = File("/Users/evgeniikorniichuk/Documents/Workspace/System/rtp-plugin/src/test/resources/Matcher/Full/MF2L_Matcher.xls")
val properties = File("/Users/evgeniikorniichuk/Documents/Workspace/System/rtp-plugin/src/test/resources/Matcher/Full/test_SIT.properties")
val config = ConfigFactory parseFile(document jfile)

val workbookPoi  = Workbook(document inputStream()) fold(
  exception =>
    s"Unable to open xls file $exception",
  workbook => {
    val transportSheet = (workbook asPoi) getSheet transport
    transportSheet rowIterator() map {
      row =>
        row cellIterator() map {
          cell =>
            cell getCellType() match {
              case CELL_TYPE_BLANK => ""
              case CELL_TYPE_NUMERIC => String valueOf (cell getNumericCellValue())
              case CELL_TYPE_STRING => cell getStringCellValue
              case CELL_TYPE_FORMULA => cell getCellFormula
              case CELL_TYPE_BOOLEAN => String valueOf (cell getBooleanCellValue)
              case CELL_TYPE_ERROR => String valueOf (cell getErrorCellValue)
            }
        } mkString ","
    } mkString "\n"
  })

val workbook  = Workbook(document inputStream()) fold(
  exception =>
    s"Unable to open xls file $exception",
  workbook => {
    val transportSheet = workbook sheetMap transport
    (((transportSheet rows) toList) sorted) map {
      row =>
        (((row cells) toList) sorted) map cellValue mkString ","
    } mkString "\n"
  })

workbookPoi unsafePerformIO()
workbook unsafePerformIO()