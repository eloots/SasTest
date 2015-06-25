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

import SASInterfaceHelpers._
import org.scalatest.Inspectors
import org.scalatest.Tag

object SasTests extends Tag("com.sequoia.sastest.SasTests")

trait PersonData {
  import formats.Formats.Date9
  import tablerowspecs.Person
  val testData1 = List(
    Person("Pjotr Brildijk",     Some(0), Some(Date9("10sep1959").toLocalDate)),
    Person("Saskia Botervloot",  Some(3), Some(Date9( "7Mar1967").toLocalDate)),
    Person("Alex Chernomanski",  None,    Some(Date9( "1FEB1964").toLocalDate)),
    Person("Sandra Vole",        None,    Some(Date9("17May1965").toLocalDate)),
    Person("Srabra Melontoe",    Some(2), Some(Date9( "8May1983").toLocalDate))
  )
}

class Test1Spec extends BaseSASSpec with PersonData with Inspectors {
  import tablerowspecs.{Person, PersonStats}

  "Calling addPersonStats on Person data" should {
    "create a table holding the average age and the total number of children" taggedAs(SasTests) in {

      val sasTestInputDataset = "sbtvpdat.testDataTableSAS"
      val sasTestOutputDataset = "sbtvpdat.testOutDataTableSAS"

      sendTestData(testData1, sasTestInputDataset, Person)

      val SASTestCode =
        s"""%include '${SASConfig.sasCodeBaseFolder}/cpHelpDS.sas';
           |%addPersonStats(personTable=${sasTestInputDataset}, outTable=${sasTestOutputDataset})
         """.stripMargin
      val SASLoggingOutput(saslogRun) = executeSASquery(sasLanguage, SASTestCode)

      val TestOutput(_, columnMapping, rawOutputData) =
        retrieveSasTable(sasTestOutputDataset, PersonStats)

      val testResult = PersonStats.mapRawToPersonStats(rawOutputData.tail, columnMapping)

      testResult.head.child_count shouldBe 5
      testResult.head.avg_age shouldBe 47.2 +- 0.0000000001

    }
  }

  "Calling 'IncrChildCountSpecial' on Person data" should {
    "increment the number of children only for persons having children" taggedAs(SasTests) in {
      val sasTestInputDataset = "sbtvpdat.testDataTableSAS"
      val sasTestOutputDataset = "sbtvpdat.testOutDataTableSAS"

      sendTestData(testData1, sasTestInputDataset, Person)

      val SASTestCode =
        s"""%include '${SASConfig.sasCodeBaseFolder}/cpHelpDS.sas';
           |%IncrChildCountSpecial(personTableIn=${sasTestInputDataset}, personTableOut=${sasTestOutputDataset})
         """.stripMargin
      val SASLoggingOutput(saslogRun) = executeSASquery(sasLanguage, SASTestCode)

      val TestOutput(_, columnMapping, rawOutputData) =
        retrieveSasTable(sasTestOutputDataset, Person)

      val testResult = Person.mapRawToPerson(rawOutputData.tail, columnMapping)

      val expected = testData1 map {
        case p @ Person(name, Some(0), birth_date) => p
        case p @ Person(name, Some(n_children), birth_date) => p.copy(n_children = Some(n_children + 1))
        case person => person
      }

      expected shouldBe testResult
    }
  }
}
