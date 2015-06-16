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
package com.sequoia.sastest.tablerowspecs

// Code generated with following command:
//
//   genSASFieldSpec --sasNumericFieldTypes child_count=Long,avg_age=Double --noMissingValueFields avg_age,child_count sbtvpdat.TESTOUTDATATABLESAS PersonStats
//
// ----- Generated code BEGIN -----
import com.sequoia.sastest._

case object PersonStats extends SASDataRowSpec {
  def sasFieldSpec = List(
    FieldSpec(sasFieldName = "avg_age", inputFieldType = InputNumericField),
    FieldSpec(sasFieldName = "child_count", inputFieldType = InputNumericField))

  val fieldIndexes: Map[String, Int] = {
    sasFieldSpec
      .map (_.sasFieldName)
      .zipWithIndex
      .toMap
  }

  val arity = sasFieldSpec.length

  def mapRawToPersonStats(data: OutputData, columnMapping: ColumnMapping): List[PersonStats] = {
    data map { values => PersonStats(values(columnMapping(0)).toDouble, values(columnMapping(1)).toLong) }
  }
}

case class PersonStats(avg_age: Double, child_count: Long) extends SASDataRowType {
  def toDelimString: String = s"""${avg_age}\t${child_count}"""
}
// ----- Generated code END   -----