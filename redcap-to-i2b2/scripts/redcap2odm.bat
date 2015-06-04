@echo off

REM Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
REM redcap2odm.bat
REM batch for execute Java app for retrieving data from RESTful web service REDCap API 
REM and create ODM XML file
REM author: Alex Wu    on 09/15/2011

@echo off
set a=conf
set a=%a%;lib\redcap2i2b2.jar
set a=%a%;lib\jdom-1.1.2.jar
set a=%a%;lib\json-rpc-1.0.jar
set a=%a%;lib\commons-lang-2.5.jar
set a=%a%;lib\commons-logging-1.1.1.jar
set a=%a%;lib\junit-4.8.2.jar
set a=%a%;lib\junit-dep-4.8.2.jar
set a=%a%;lib\log4j.jar
set classpath=%a%

@echo on
@echo CLASSPATH=%classpath%
@echo Starting calling RESTful ws API ...
@echo off

REM call Redcap RESTful WS API and return string in json data format
REM java -classpath %CLASSPATH% com.recomdata.redcap.ws.GetRedcapService
REM java -classpath %CLASSPATH% org.junit.runner.JUnitCore com.recomdata.redcap.ws.GetRedcapServiceTest
REM java -classpath %CLASSPATH% com.recomdata.redcap.odm.Redcap2ODMTest

java -classpath %CLASSPATH% com.recomdata.redcap.odm.Redcap2ODM
 
REM exit