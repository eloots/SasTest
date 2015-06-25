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

import com.sequoia.sastest.{SASCharacterField, InputCharacterField, InputNumericField}
import com.sequoia.sastest.tablerowspecs._
//import com.sequoia.sastest.RowTypes.{SASDataRowType, SASDataRowSpec}

case object Car extends SASDataRowSpec {
  def sasFieldSpec = List(
    FieldSpec(sasFieldName = "Cylinders", inputFieldType = InputNumericField),
    FieldSpec(sasFieldName = "DriveTrain", inputFieldType = InputCharacterField, sasFieldLength = "$5.", sasFieldType = SASCharacterField),
    FieldSpec(sasFieldName = "EngineSize", inputFieldType = InputNumericField),
    FieldSpec(sasFieldName = "Horsepower", inputFieldType = InputNumericField),
    FieldSpec(sasFieldName = "Invoice", inputFieldType = InputNumericField, sasFieldFormat = "DOLLAR"),
    FieldSpec(sasFieldName = "Length", inputFieldType = InputNumericField),
    FieldSpec(sasFieldName = "MPG_City", inputFieldType = InputNumericField),
    FieldSpec(sasFieldName = "MPG_Highway", inputFieldType = InputNumericField),
    FieldSpec(sasFieldName = "MSRP", inputFieldType = InputNumericField, sasFieldFormat = "DOLLAR"),
    FieldSpec(sasFieldName = "Make", inputFieldType = InputCharacterField, sasFieldLength = "$13.", sasFieldType = SASCharacterField),
    FieldSpec(sasFieldName = "Model", inputFieldType = InputCharacterField, sasFieldLength = "$40.", sasFieldType = SASCharacterField),
    FieldSpec(sasFieldName = "Origin", inputFieldType = InputCharacterField, sasFieldLength = "$6.", sasFieldType = SASCharacterField),
    FieldSpec(sasFieldName = "Type", inputFieldType = InputCharacterField, sasFieldLength = "$8.", sasFieldType = SASCharacterField),
    FieldSpec(sasFieldName = "Weight", inputFieldType = InputNumericField),
    FieldSpec(sasFieldName = "Wheelbase", inputFieldType = InputNumericField))

  val fieldIndexes: Map[String, Int] = {
    (sasFieldSpec
      .map (_.sasFieldName)).zipWithIndex
      .toMap
  }

  val arity = sasFieldSpec.length
}

case class Car(cylinders: Option[Int], driveTrain: String, engineSize: Double, horsePower: Int, invoice: Int, length: Int,
               mpg_City: Int, mpg_Highway: Int, msrp: Int, make: String, model: String, origin: String, carType: String, weight: Int, wheelbase: Int ) {
  def toDelimString: String = s"""$cylinders\t$driveTrain\t$engineSize\t$horsePower\t$invoice\t$length\t$mpg_City\t$mpg_Highway\t$msrp\t$make\t$model\t$origin\t$carType\t$weight\t$wheelbase"""
}