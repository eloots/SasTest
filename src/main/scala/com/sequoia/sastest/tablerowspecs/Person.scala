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

import java.time.LocalDate

import com.sequoia.sastest.{InputNumericField, SASCharacterField, InputCharacterField}
import com.sequoia.sastest.formats.Formats._

case object Person extends SASDataRowSpec {
  def sasFieldSpec = List(
    FieldSpec(
      sasFieldName = "NAME",
      inputFieldType = InputCharacterField,
      sasFieldLength = "$40.",
      sasFieldType = SASCharacterField),
    FieldSpec(
      sasFieldName = "N_CHILDREN",
      inputFieldType = InputNumericField),
    FieldSpec(
      sasFieldName = "BIRTH_DATE",
      inputFieldType = InputCharacterField,
      sasFieldInformat = "DATE9.",
      sasFieldFormat = "DATE9.")
  )

  val fieldIndexes: Map[String, Int] = {
    (sasFieldSpec
      .map (_.sasFieldName)).zipWithIndex
      .toMap
  }

  val arity = sasFieldSpec.length
}


/**
 * Person is an example of a (case) class used to model a row in a SAS table. Missing values are modelled
 * by making the relevant column of type Option[xxx]
 * @param name       Considered in this case to be a primary key, no missing values are permitted
 * @param nChildren  Optional number of children count
 * @param birthDate  Optional birthdate
 */
case class Person(name: String, nChildren: Option[Int] = None, birthDate: Option[LocalDate] = None) extends SASDataRowType {
  def toDelimString: String = s"""${name}\t${nChildren.getOrElse("")}\t${formattedDate(birthDate, DATE9)}"""
}
