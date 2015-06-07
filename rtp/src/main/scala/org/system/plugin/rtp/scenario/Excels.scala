package org.system.plugin.rtp.scenario

import info.folone.scala.poi.{Cell, StringCell}

import scala.language.postfixOps

/**
 * Created by evgeniikorniichuk on 02/05/15.
 */
trait Excels {

  def getValuesForStringCells(seq: Seq[StringCell]): Seq[String] = {
    seq map (cell => cell data)
  }

  def stringCellsFromRow(seq: Seq[Cell]) = {
    for {
      cell <- seq if (cell isInstanceOf)[StringCell]
    } yield (cell asInstanceOf)[StringCell]
  }

}
