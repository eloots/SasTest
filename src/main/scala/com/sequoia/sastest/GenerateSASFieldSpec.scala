/**
 * Copyright 2015 Eric Loots
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sequoia.sastest

import com.sequoia.sastest.SASInterfaceHelpers._
import com.sequoia.sastest.tablerowspecs._

case object SASContentTableRow extends SASDataRowSpec {
  def sasFieldSpec = List(
    FieldSpec(
      sasFieldName = "LIBNAME",
      inputFieldType = InputCharacterField,
      sasFieldLength = "$8.",
      sasFieldType = SASCharacterField),
    FieldSpec(
      sasFieldName = "MEMNAME",
      inputFieldType = InputCharacterField,
      sasFieldLength = "$32.",
      sasFieldType = SASCharacterField),
    FieldSpec(
      sasFieldName = "NAME",
      inputFieldType = InputCharacterField,
      sasFieldLength = "$32.",
      sasFieldType = SASCharacterField),
    FieldSpec(
      sasFieldName = "TYPE",
      inputFieldType = InputNumericField,
      sasFieldType = SASNumericField),
    FieldSpec(
      sasFieldName = "LENGTH",
      inputFieldType = InputNumericField,
      sasFieldType = SASNumericField),
    FieldSpec(
      sasFieldName = "FORMAT",
      inputFieldType = InputCharacterField,
      sasFieldLength = "$32.",
      sasFieldType = SASCharacterField),
    FieldSpec(
      sasFieldName = "INFORMAT",
      inputFieldType = InputCharacterField,
      sasFieldLength = "$32.",
      sasFieldType = SASCharacterField),
    FieldSpec(sasFieldName = "MEMLABEL"),
    FieldSpec(sasFieldName = "TYPEMEM"),
    FieldSpec(sasFieldName = "VARNUM"),
    FieldSpec(sasFieldName = "LABEL"),
    FieldSpec(sasFieldName = "FORMATD"),
    FieldSpec(sasFieldName = "FORMATL"),
    FieldSpec(sasFieldName = "INFORML"),
    FieldSpec(sasFieldName = "INFORMD"),
    FieldSpec(sasFieldName = "JUST"),
    FieldSpec(sasFieldName = "NPOS"),
    FieldSpec(sasFieldName = "NOBS"),
    FieldSpec(sasFieldName = "ENGINE"),
    FieldSpec(sasFieldName = "CRDATE"),
    FieldSpec(sasFieldName = "MODATE"),
    FieldSpec(sasFieldName = "DELOBS"),
    FieldSpec(sasFieldName = "IDXUSAGE"),
    FieldSpec(sasFieldName = "MEMTYPE"),
    FieldSpec(sasFieldName = "IDXCOUNT"),
    FieldSpec(sasFieldName = "PROTECT"),
    FieldSpec(sasFieldName = "FLAGS"),
    FieldSpec(sasFieldName = "COMPRESS"),
    FieldSpec(sasFieldName = "REUSE"),
    FieldSpec(sasFieldName = "SORTED"),
    FieldSpec(sasFieldName = "SORTEDBY"),
    FieldSpec(sasFieldName = "CHARSET"),
    FieldSpec(sasFieldName = "COLLATE"),
    FieldSpec(sasFieldName = "INFORMD"),
    FieldSpec(sasFieldName = "INFORMD"),
    FieldSpec(sasFieldName = "NODUPKEY"),
    FieldSpec(sasFieldName = "NODUPREC"),
    FieldSpec(sasFieldName = "ENCRYPT"),
    FieldSpec(sasFieldName = "POINTOBS"),
    FieldSpec(sasFieldName = "GENMAX"),
    FieldSpec(sasFieldName = "GENNUM"),
    FieldSpec(sasFieldName = "GENNEXT"),
    FieldSpec(sasFieldName = "TRANSCOD")
  )

  val fieldIndexes: Map[String, Int] = {
    (sasFieldSpec
      .map (_.sasFieldName)).zipWithIndex
      .toMap
  }

  val arity = sasFieldSpec.length
}

case class SASContentTableRow(libname: String, memName: String, name: String, colType: Int, length: Int, format: String, inFormat: String)

object GenerateSASFieldSpec {
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      println(s"Usage: genSpec libname.tableName tableName")
      System.exit(-1)
    }
    val tableFullName = args(0)
    val tableName = args(1)

    val host: String = "sbisas9463.sbiconsulting.be"
    val port: Int = 8591
    val sasHost = SASHost(host, port)
    val sasCredentials = SASCredentials("lootser", "ku-floj-tuerch-ad-mec")

    implicit val SASContext(sasLanguage, ctx, adm) = sasContext(sasHost.host, sasHost.port, sasCredentials.userName, sasCredentials.password)

    val sasContentTable = "WORK.SAS_CONTENT"
    val sasCode =
      s"""libname sbtvpdat "/opt/SAS/projects/SBI/TrainingVAjan2014/PRD/Data";
         |proc contents data=${tableFullName} out=${sasContentTable} noprint;run;""".stripMargin
    val SASLoggingOutput(saslog) = executeSASquery(sasLanguage, sasCode)
    val TestOutput(_, columnMapping, rawOutputData) =
      retrieveSasTable(sasContentTable, SASContentTableRow)

    val columnSpecs = rawOutputData.tail
      .map {
      case values => SASContentTableRow(values(columnMapping(0)), values(columnMapping(1)), values(columnMapping(2)), values(columnMapping(3)).toInt, values(columnMapping(4)).toInt, values(columnMapping(5)), values(columnMapping(6)))
    }

    val inputItems = columnSpecs map {
      case SASContentTableRow(_, _, colName, 1, _, format, "") =>
        if (format != "")
          s"""FieldSpec(sasFieldName = "${colName}", inputFieldType = InputNumericField, sasFieldFormat = "${format}")"""
        else
          s"""FieldSpec(sasFieldName = "${colName}", inputFieldType = InputNumericField)"""
      case SASContentTableRow(_, _, colName, 1, _, format, inFormat) =>
        if (format != "")
          s"""FieldSpec(sasFieldName = "${colName}", inputFieldType = InputCharacterField, sasFieldInformat = "${inFormat}", sasFieldFormat = "${format}")"""
        else
          s"""FieldSpec(sasFieldName = "${colName}", inputFieldType = InputCharacterField, sasFieldInformat = "${inFormat}")"""
      case SASContentTableRow(_, _, colName, 2, length, _, _) =>
        s"""FieldSpec(sasFieldName = "${colName}", inputFieldType = InputCharacterField, sasFieldLength = "$$${length}.", sasFieldType = SASCharacterField)"""
    }
    //println(inputItems.mkString("", ",\n", "\n"))

    val code = s"""
       |  case object ${tableName} extends HelperFunctions {
       |    def sasFieldSpec = List(
       |    ${inputItems.mkString("  ", ",\n      ", ")")}
       |
       |    val fieldIndexes: Map[String, Int] = {
       |      (sasFieldSpec
       |        .map (_.sasFieldName)).zipWithIndex
       |        .toMap
       |    }
       |
       |    val arity = sasFieldSpec.length
       |  }
     """.stripMargin

    println(code)

    ctx.close()
    adm.shutdown()
  }

}
