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
package com.sequoia

import com.sas.iom.SAS.ILanguageServicePackage.{LineTypeSeqHolder, CarriageControlSeqHolder}
import com.sas.iom.SAS.{IWorkspaceHelper, IWorkspace, ILanguageService}
import com.sas.iom.SASIOMDefs.StringSeqHolder
import com.sas.services.connection._
import scala.collection.JavaConverters._


package object sastest {
  object SASConfig {
    import com.typesafe.config._

    val config = ConfigFactory.load()
    val sasHostname = config.getString("SASTest.Environment.hostname")
    val sasPort     = config.getInt("SASTest.Environment.port")
    val sasUsername = config.getString("SASTest.Environment.sasUsername")
    val sasPassword = config.getString("SASTest.Environment.sasPassword")

    val tempFileBaseFolder = config.getString("SASTest.Commons.tempFileBaseFolder")
    val sasCodeBaseFolder  = config.getString("SASTest.Commons.sasCodeBaseFolder")

    private val sasLibrariesConfig = config.getObjectList("SASTest.Libraries.libs").asScala.map (_.toConfig)
    private val sasLibraryMap = {
      for {
        library <- sasLibrariesConfig
      } yield library.getString("libname") -> library.getString("libpath")
    } toMap
    val sasLibraryCode = {
      for {
        (libName, libPath) <- sasLibraryMap
      } yield s"""libname $libName "$libPath";"""
    } mkString("\n")
  }

  type ColumnMapping = Map[Int, Int]
  type OutputData = List[Array[String]]
  type FieldIndexes = Map[String, Int]

  sealed trait SASFieldType
  case object SASNumericField extends SASFieldType
  case object SASCharacterField extends SASFieldType

  sealed trait InputFieldType
  case object InputNumericField extends InputFieldType
  case object InputCharacterField extends InputFieldType

  case class SASHost(host: String, port: Int)
  case class SASCredentials(userName: String, password: String)
  case class SASContext(language: ILanguageService, context: ConnectionInterface, admin: ConnectionFactoryAdminInterface)
  case class  SASCommand(query: String)
  case class  SASLoggingOutput(logLines: String)

  def sasContext(host: String, port: Int, userName: String, password: String): SASContext = {
    val classID: String = Server.CLSID_SAS
    val server: Server = new BridgeServer(classID, host, port)
    val cxfConfig: ConnectionFactoryConfiguration = new ManualConnectionFactoryConfiguration(server)
    val cxfManager: ConnectionFactoryManager = new ConnectionFactoryManager
    val cxf: ConnectionFactoryInterface = cxfManager.getFactory(cxfConfig)
    val admin: ConnectionFactoryAdminInterface = cxf.getAdminInterface
    val context: ConnectionInterface = cxf.getConnection(userName, password)
    val obj: org.omg.CORBA.Object = context.getObject
    val iWorkspace: IWorkspace = IWorkspaceHelper.narrow(obj)
    val sasLanguage: ILanguageService = iWorkspace.LanguageService
    SASContext(sasLanguage, context, admin)
  }

  def executeSASquery(language: ILanguageService, query: String): SASLoggingOutput = {
    val codeWithSASLibraryDefinitionsPrepended = s"""${SASConfig.sasLibraryCode}
                             |${query}""".stripMargin
    language.Submit(codeWithSASLibraryDefinitionsPrepended)
    val logCarriageControlHolder: CarriageControlSeqHolder = new CarriageControlSeqHolder
    val logLineTypeHolder: LineTypeSeqHolder = new LineTypeSeqHolder
    val logHolder: StringSeqHolder = new StringSeqHolder
    language.FlushLogLines(Integer.MAX_VALUE, logCarriageControlHolder, logLineTypeHolder, logHolder)
    val logLines: Array[String] = logHolder.value
    SASLoggingOutput(logLines.mkString("\n"))
  }
}
