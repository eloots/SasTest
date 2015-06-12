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

import java.io.{FileOutputStream, OutputStreamWriter}
import java.nio.charset.StandardCharsets

object DelimitedFileRW {
  def writeDelimitedFile(results: List[String], fileName: String): Unit = {
    def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
      val p = new java.io.PrintWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8), true)
      try { op(p) } finally { p.close() }
    }

    import java.io._
    printToFile(new File(fileName)) { p =>
      results.foreach(line => p.println(line))
    }
  }
}

import scala.language.postfixOps
import java.io.BufferedReader
import java.io.FileReader
import java.io.File

/**
 * FileLineTraversable
 * Source: Scala in Depth by Joshua Suereth
 */
class FileLineTraversable(file: File) extends Traversable[String] {
  override def foreach[U](f: String => U): Unit = {
    val input = new BufferedReader(new FileReader(file))
    try {
      var line = input.readLine
      while (line != null) {
        f(line)
        line = input.readLine
      }
    } finally {
      input.close()
    }
  }

  override def toString =
    "{Lines of " + file.getAbsolutePath + "}"
}