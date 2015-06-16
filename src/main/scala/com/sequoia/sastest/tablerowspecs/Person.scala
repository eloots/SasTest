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
//   genSASFieldSpec --sasNumericFieldTypes n_children=Long --noMissingValueFields name sbtvpdat.TESTDATATABLESAS Person
//
// ----- Generated code BEGIN -----
import com.sequoia.sastest._
import com.sequoia.sastest.formats.Formats._
import java.time.LocalDate

case object Person extends SASDataRowSpec {
  def sasFieldSpec = List(
    FieldSpec(sasFieldName = "name", inputFieldType = InputCharacterField, sasFieldLength = "$40.", sasFieldType = SASCharacterField),
    FieldSpec(sasFieldName = "n_children", inputFieldType = InputNumericField),
    FieldSpec(sasFieldName = "birth_date", inputFieldType = InputCharacterField, sasFieldInformat = "DATE9.", sasFieldFormat = "DATE9."))

  val fieldIndexes: Map[String, Int] = {
    sasFieldSpec
      .map (_.sasFieldName)
      .zipWithIndex
      .toMap
  }

  val arity = sasFieldSpec.length

  def mapRawToPerson(data: OutputData, columnMapping: ColumnMapping): List[Person] = {
    data map { values => Person(values(columnMapping(0)), if (values(columnMapping(1)) == "") None else Some(values(columnMapping(1)).toLong), if (values(columnMapping(2)) == "") None else Some(Date9(values(columnMapping(2))).toLocalDate)) }
  }
}

case class Person(name: String, n_children: Option[Long] = None, birth_date: Option[LocalDate] = None) extends SASDataRowType {
  def toDelimString: String = s"""${name}\t${n_children.getOrElse("")}\t${formattedDate(birth_date, DATE9)}"""
}

// ----- Generated code END   -----
//import com.sequoia.sastest.{InputNumericField, SASCharacterField, InputCharacterField}
//import com.sequoia.sastest.formats.Formats._
//
//case object Person extends SASDataRowSpec {
//  def sasFieldSpec = List(
//    FieldSpec(
//      sasFieldName = "NAME",
//      inputFieldType = InputCharacterField,
//      sasFieldLength = "$40.",
//      sasFieldType = SASCharacterField),
//    FieldSpec(
//      sasFieldName = "N_CHILDREN",
//      inputFieldType = InputNumericField),
//    FieldSpec(
//      sasFieldName = "BIRTH_DATE",
//      inputFieldType = InputCharacterField,
//      sasFieldInformat = "DATE9.",
//      sasFieldFormat = "DATE9.")
//  )
//
//  val fieldIndexes: Map[String, Int] = {
//    sasFieldSpec
//      .map (_.sasFieldName)
//      .zipWithIndex
//      .toMap
//  }
//
//  val arity = sasFieldSpec.length
//}
//
///**
// * Person is an example of a (case) class used to model a row in a SAS table. Missing values are modelled
// * by making the relevant column of type Option[xxx]
// * @param name       Considered in this case to be a primary key, no missing values are permitted
// * @param n_children  Optional number of children count
// * @param birth_date  Optional birthdate
// */
//case class Person(name: String, n_children: Option[Long] = None, birth_date: Option[LocalDate] = None) extends SASDataRowType {
//  def toDelimString: String = s"""${name}\t${n_children.getOrElse("")}\t${formattedDate(birth_date, DATE9)}"""
//}
