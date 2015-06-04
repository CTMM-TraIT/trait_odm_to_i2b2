@echo off

REM Copyright(c)  2011-2012 Recombinant Data Corp., All rights Reserved
REM odm2i2b2.bat
REM batch for execute Java app for push ODM into i2b2
REM author: Alex Wu    on 09/13/2011

if "%1" == "" goto error

set arg1=%1

set a=conf
set a=%a%;lib\redcap2i2b2.jar
set a=%a%;lib\jdom-1.1.2.jar
set a=%a%;lib\json-rpc-1.0.jar
set a=%a%;lib\commons-lang-2.5.jar
set a=%a%;lib\commons-logging-1.1.1.jar
set a=%a%;lib\log4j.jar

set classpath=%a%

@echo on
@echo CLASSPATH=%classpath%
@echo off

REM java -classpath %CLASSPATH% com.recomdata.i2b2.I2B2ODMStudyHandlerCMLClient C:/redcap2i2b2/files/inbox/CHB_REDCap_2.xml

java -classpath %CLASSPATH% com.recomdata.i2b2.I2B2ODMStudyHandlerCMLClient %arg1%

if not "%1" == "" goto end

:error
echo missing argument!
echo usage: please type ODM file as argument ...

:end
echo Done.

REM exit