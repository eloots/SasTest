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

package object tablerowspecs {
  trait SASDataRowType {
    def toDelimString: String
  }

  trait SASDataRowSpec {
    def sasFieldSpec: List[FieldSpec]
    def arity: Int
    def fieldIndexes: FieldIndexes
  }

  case class FieldSpec( sasFieldName: String,
                        inputFieldType: InputFieldType = InputNumericField,
                        sasFieldLength: String = "",
                        sasFieldInformat: String = "",
                        sasFieldFormat: String = "",
                        sasFieldType: SASFieldType = SASNumericField
                      )
}
