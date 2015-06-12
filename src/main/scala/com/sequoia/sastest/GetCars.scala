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

object GetCars {
  def main(args: Array[String]): Unit = {

    import tablerowspecs.Car
    val host: String = "sbisas9463.sbiconsulting.be"
    val port: Int = 8591
    val sasHost = SASHost(host, port)
    val sasCredentials = SASCredentials("lootser", "ku-floj-tuerch-ad-mec")

    implicit val SASContext(sasLanguage, ctx, adm) = sasContext(sasHost.host, sasHost.port, sasCredentials.userName, sasCredentials.password)

    val TestOutput(_, columnMapping, rawOutputData) =
      retrieveSasTable("SASHELP.CARS", Car)

    val cars = rawOutputData.tail
      .map {
      case values =>
        //println(values.toList)
        val car = Car(
          { val cyls = values(columnMapping(0)); if (cyls == "") None else Some(cyls.toInt)},
          values(columnMapping(1)),
          values(columnMapping(2)).toDouble,
          values(columnMapping(3)).toInt,
          values(columnMapping(4)).replaceAll("\\$","").replaceAll(",","").toInt,
          values(columnMapping(5)).toInt,
          values(columnMapping(6)).toInt,
          values(columnMapping(7)).toInt,
          values(columnMapping(8)).replaceAll("\\$","").replaceAll(",","").toInt,
          values(columnMapping(9)),
          values(columnMapping(10)),
          values(columnMapping(11)),
          values(columnMapping(12)),
          values(columnMapping(13)).toInt,
          values(columnMapping(14)).toInt
        )
        println(car)
        car
    }
    println(cars.mkString("\n"))
  }
}
