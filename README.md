ScalaTest for SAS unit & integration tests
==========================================

###Overview

Demonstration of using ScalaTest for writing Unit and integration tests for  applications written in Base SAS.

###Requirements

Obviously, a SAS server is required to use this application. Also, an account with the appropriate rights at the SAS server level + OS level is needed.

The example test ```Test1Spec.scala``` tests a simple piece of SAS code. The path of this test code as used in the test code should match the location of the SAS code on the server.

```scala
val SASTestCode =
        s"""%include '/home/lootser/sasuser.v94/FOD_VVVL/code/cpHelpDS.sas';
           |%addPersonStats(personTable=${sasTestInputDataset}, outTable=${sasTestOutputDataset})
         """.stripMargin
      val SASLoggingOutput(saslogRun) = executeSASquery(sasLanguage, SASTestCode)
```

The contents of the ```cpHelpDS.sas``` file is as follows:

```
libname olib "/opt/SAS/projects/SBI/TrainingVAjan2014/PRD/Data";

%macro copySASHelpDS(datasetName=);
  data olib.&datasetName.;
    set sashelp.&datasetName.;
%mend copySASHelpDS;

%macro addPersonStats(personTable=, outTable=);
  proc sql;
    create table &outTable. as
      select
        avg(floor(YRDIF(BIRTH_DATE, '26MAY2015'd, 'AGE'))) as AVG_AGE,
        sum(N_CHILDREN) as CHILD_COUNT
      from &personTable. as PERSON
    ;
  quit;

%mend addPersonStats;
```

Note that, in the present version of the program, the tests should be run from an sbt session running on the SAS server. In other words, it's not possible (yet) to run these remotely.
