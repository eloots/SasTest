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

//import com.sequoia.sastest.RowTypes.{SASDataRowType, SASDataRowSpec}
import com.sequoia.sastest.{SASNumericField, InputNumericField}
import com.sequoia.sastest.tablerowspecs._

case object PersonStats extends SASDataRowSpec {
  def sasFieldSpec = List(
    FieldSpec(
      sasFieldName = "CHILD_COUNT",
      inputFieldType = InputNumericField,
      sasFieldType = SASNumericField),
    FieldSpec(
      sasFieldName = "AVG_AGE",
      inputFieldType = InputNumericField,
      sasFieldType = SASNumericField)
  )

  val fieldIndexes: Map[String, Int] = {
    (sasFieldSpec
      .map (_.sasFieldName)).zipWithIndex
      .toMap
  }

  val arity = sasFieldSpec.length
}

case class PersonStats(child_count: Int, avg_age: Double) extends SASDataRowType {
  def toDelimString: String = s"""$child_count\t$avg_age"""
}