# Project martinfowler_uiArchs

## What it does ##
These are sample implementations of the UIs described in https://www.martinfowler.com/eaaDev/uiArchs.html .

## How to run the sample application ##
First initialize the DB backend by running the following DB scripts (from test/resources/db_scripts/YOURDBMS/, where YOURDBMS is your preferred DBMS):
 * as privileged DB-user (e. g. root): 1_init_db.sql
 * as privileged DB-user (e. g. root): 2_init_user.sql
 * as privileged DB-user or new application-user "uiuser": 3_insert_db_testdata.sql

On running "gradle run" in the project base directory it creates and opens the Java FX based UI specified in build.gradle under "application{ mainClass = ... }".


imperfectsilentart.martinfowler.uiArchs.GenericConnectionPoolTest.java is a modified version of https://github.com/oracle/oracle-db-examples/blob/master/java/jdbc/ConnectionSamples/ADBQuickStart.java , which is licensed under the Universal Permissive License v 1.0.