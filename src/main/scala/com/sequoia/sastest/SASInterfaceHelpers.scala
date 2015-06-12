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

import java.io.File
import java.util.UUID

import com.sas.iom.SAS.ILanguageService
import org.apache.commons.io.FileUtils
import com.sequoia.sastest.tablerowspecs._


trait HelperFunctions {

}

object SASInterfaceHelpers {

  import tablerowspecs._

  def sasImportCode(testdataTableName: String, inputDataFileName: String, sasFieldNames: List[FieldSpec]): String = {
    val pre = sasFieldNames flatMap {
      case FieldSpec(name, _, length, informat, format, ftype) =>
        List(
          if (ftype == SASNumericField) s"length $name 8" else s"length $name $length",
          if (informat != "") s"informat $name $informat" else "",
          if (format != "") s"format $name $format" else ""
        )
    } filter (_ != "")

    val post = sasFieldNames map {
      case FieldSpec(name, InputNumericField,_, _, _, _) =>
        s"$name"
      case FieldSpec(name, InputCharacterField,_, _, _, _) =>
        s"$name $$"
    }

    s"""
        LIBNAME SBTVPDAT "/opt/SAS/projects/SBI/TrainingVAjan2014/PRD/Data";
        DATA ${testdataTableName};
          ${pre.mkString("", ";\n  ", ";")}
          infile '${inputDataFileName}' dlm='09'X dsd truncover;
          input
            ${post.mkString("", "\n    ", ";\n")}
        RUN;
       """.stripMargin
  }

  case class TestOutput(sasLoggingOutput: SASLoggingOutput, columnMapping: ColumnMapping, outputData: OutputData)

  def sendTestData(
                    testData: List[tablerowspecs.SASDataRowType],
                    sasTestInputDataset: String,
                    fieldSpec: List[FieldSpec]
                    )(implicit sasLanguage: ILanguageService): SASLoggingOutput = {

    val inputDelimitedFileName = s"/home/lootser/sasuser.v94/FOD_VVVL/testData/${UUID.randomUUID()}"
    val testDataCSV = testData.map (person => person.toDelimString)
    DelimitedFileRW.writeDelimitedFile(testDataCSV, inputDelimitedFileName)
    val importCode = sasImportCode(sasTestInputDataset, inputDelimitedFileName, fieldSpec)
    val sasLoggingOutput = executeSASquery(sasLanguage, importCode)
    FileUtils.forceDelete(new java.io.File(inputDelimitedFileName))
    sasLoggingOutput
  }

  def retrieveSasTable(sasTestOutputDataset: String,
                       sasDataRowSpec: SASDataRowSpec
                      )(implicit sasLanguage: ILanguageService): TestOutput = {

    val outputDelimitedFileName = s"/home/lootser/sasuser.v94/FOD_VVVL/testData/${UUID.randomUUID()}"
    val SASexportCode =
      s"""libname sbtvpdat "/opt/SAS/projects/SBI/TrainingVAjan2014/PRD/Data";
         |
         |proc export data=${sasTestOutputDataset} replace
         |	outfile='${outputDelimitedFileName}'
         |	dbms=dlm;
         |	delimiter='09'x;
         |run;
         """.stripMargin
    val sasLoggingOutput = executeSASquery(sasLanguage, SASexportCode)
    val rawOutputData = new FileLineTraversable(new File(outputDelimitedFileName)).map(_.split("\t", sasDataRowSpec.arity)).toList
    val columnMapping = (rawOutputData.head.zipWithIndex.map {case (fld, index) => sasDataRowSpec.fieldIndexes(fld) -> index}).toMap
    FileUtils.forceDelete(new java.io.File(outputDelimitedFileName))
    TestOutput(sasLoggingOutput, columnMapping, rawOutputData)
  }
}