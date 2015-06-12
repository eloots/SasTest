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
package com.sequoia.sastest.formats

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object Formats {
  val DATE9 = DateTimeFormatter.ofPattern("ddMMMyyyy")

  case class Date9(date: String)

  implicit class Date9ToLocalDate(date: Date9) {
    def toLocalDate = {
      val validMonths = Vector("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")
      val date9Format = """([1-9]|\d\d)(\w{3})(\d{4})""".r
      val date9Format(d, m, y) = date.date
      val month =
        if (validMonths.contains(m.toUpperCase))
          validMonths.indexOf(m.toUpperCase) + 1
        else throw new IllegalArgumentException(s"Invalid month specification '$m' in ${date.date}")
      LocalDate.of(y.toInt,month,d.toInt)
    }
  }

  def formattedDate(date: Option[LocalDate], format: DateTimeFormatter): String = {
    date match {
      case Some(date: LocalDate) => date.format(format)
      case _ => ""
    }
  }
}
