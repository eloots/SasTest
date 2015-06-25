ScalaTest for SAS unit & integration tests
==========================================

###Overview

Demonstration of using ScalaTest for writing Unit and integration tests for  applications written in Base SAS. This code cannot be executed remotely. In other words, it should be run on a SAS server.

###Requirements

Obviously, a SAS server is required to use this application. Also, an account with the appropriate rights at the SAS server level + OS level is needed. Access to SAS libraries is needed and a folder that can we accessed and written by the user running the tests is required.

The configuration of the test environment is done by modifying the ```application.conf``` file under the ```src/main/resources``` folder.

Here's an example of such a configuration file:

```
SASTest {
  Environment {
    hostname    = myfavoriteSasServer.myorg.com
    port        = 8591
    sasUsername = johnAppleseed
    sasPassword = applesAreOkForY0u
  }

  Commons {
    tempFileBaseFolder = /opt/SAS/testTempFiles
    sasCodeBaseFolder  = /opt/SAS/projects/Apples/codeBase
  }

  Libraries {
    libs = [
      { libname = TAPPDAT, libpath = /opt/SAS/projects/Apples/TST/Data }
      { libname = TAPPSTA, libpath = /opt/SAS/projects/Apples/TST/Staging }
      { libname = TAPPCOM, libpath = /opt/SAS/projects/Apples/TST/Common }

    ]
  }
}
```

The section ```Libraries.libs``` contains the SAS LibRefs and associated path on the server. In the example above, 3 entries have been defined. Based on this section, SAS ```LIBNAME``` statements are generated which will be prepended to any code submitted to the SAS server.

###Running tests

The example test ```Test1Spec.scala``` tests a simple piece of SAS code. The path of this test code as used in the test code should match the location of the SAS code on the server.



```scala
val SASTestCode =
        s"""%include '/opt/SAS/projects/Apples/codeBase/cpHelpDS.sas';
           |%addPersonStats(personTable=${sasTestInputDataset}, outTable=${sasTestOutputDataset})
         """.stripMargin
      val SASLoggingOutput(saslogRun) = executeSASquery(sasLanguage, SASTestCode)
```

The contents of the ```cpHelpDS.sas``` file is as follows:

```
%macro addPersonStats(personTable=, outTable=);
%macro IncrChildCountSpecial(personTableIn=, personTableOut=);
  PROC SQL;
     CREATE TABLE &personTableOut. AS
     SELECT t1.name,
            /* n_children */
              (case when not missing(t1.n_children) and t1.n_children ^= 0
                then t1.n_children + 1
                else t1.n_children
              end) AS n_children,
            t1.birth_date
        FROM &personTableIn. t1;
  QUIT;
%mend IncrChildCountSpecial;

%macro addPersonStats(personTable=, outTable=);
	proc sql;
		create table &outTable. as
			select
				avg(floor(YRDIF(BIRTH_DATE, '26MAY2015'd, 'AGE'))) as AVG_AGE,
				sum(N_CHILDREN) as CHILD_COUNT format 5.
			from &personTable. as PERSON
	;
	quit;

%mend addPersonStats;
```

Note that, in the present version of the program, the tests should be run from an sbt session running on the SAS server. In other words, it's not possible (yet) to run these remotely.

###Generation of Scala helper class/object

One of the repetitive tasks that needs to be done when writing tests is to define Scala case classes and objects that model the structure of SAS tables used in the tests. As this is a rather time-consuming and boring job, a helper function was written that generates all this code based on the metadata of a SAS table.

This is useful in most test development scenarios, as, normally, tables have already been set-up in SAS.

Because SAS only has two column types, namely Character (for fixed length char type columns) and Numeric (encoded as a 64-bit number) -and- uses so-called SAS formats to fine tune the meaning of columns, a mapping of SAS column types to Scala types cannot be done without providing extra information to the code generator.

Missing values in table columns are fully supported. Again the code generator needs additional information to generate the appropriate code.

Let's illustrate this with a simple example.

Suppose we have a SAS table ```PERSON``` with three columns in SAS library ```TAPPDAT```:

```
	Column Name     Type	    Length     Format
	name            Char          40
	n_children      Numeric        8
	birth_date      Numeric        8       DATE9
```

Suppose column ```name``` cannot have missing values, whereas ```n_children``` and ```birth_date``` can. Also, we want to model the ```n_children``` column in Scala as a ```Long```.

Generating the code from ```sbt``` is done as follows:

```
$ sbt
[info] Loading project definition from /home/user/ScalaProjects/SAS_Test/project
[info] Set current project to sasUnitTesting (in build file:/home/user/ScalaProjects/SAS_Test/)
> run --sasNumericFieldTypes n_children=Long --noMissingValueFields name TAPPDAT.PERSON Person

[info] Running com.sequoia.sastest.GenerateSASFieldSpec --sasNumericFieldTypes n_children=Long --noMissingValueFields name sbtvpdat.TESTDATATABLESAS Person
[info] Generated code written to file: ../Person.scala
```

Note that, by default, code is generated to deal with missing values. Also, in the above example, for column ```name``` of type Char, the code generator can find all required info from the SAS metadata whereas for column ```birth_date```, the FORMAT info in the metadata leads to a mapping to a Java type of ```LocalDate```.

The generated code is be written to a file name ```Person.scala``` one-level up from the project folder and looks as follows:

```scala
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
    data map { values => Person(
        values(columnMapping(0)),
        if (values(columnMapping(1)) == "") None else Some(values(columnMapping(1)).toLong),
        if (values(columnMapping(2)) == "") None else Some(Date9(values(columnMapping(2))).toLocalDate))
      }
  }
}

case class Person(name: String, n_children: Option[Long] = None, birth_date: Option[LocalDate] = None) extends SASDataRowType {
  def toDelimString: String = s"""${name}\t${n_children.getOrElse("")}\t${formattedDate(birth_date, DATE9)}"""
}
// ----- Generated code END   -----
```

###Notes

It should be stressed that, for the DATE9 format, helper code was written to support conversion of dates between SAS and Scala. Obviously, other SAS formats can be supported (e.g. DATETIME21.0). The code for the DATE9 format can be used a inspiration.
