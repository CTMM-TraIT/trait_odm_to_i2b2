Users information
================
This page is meant for users who want to install and use ODM-to-i2b2 in order to convert an ODM file to
three tabular files.

Windows:
```sh
$ cd C:\path\to\workspace
$ mkdir ODM-to-i2b2
$ cd ODM-to-i2b2
$ mkdir input-ODMs
$ mkdir output-tabular-files
$ git clone https://github.com/CTMM-TraIT/trait_odm_to_i2b2.git
$ cd trait_odm_to_i2b2
$ git checkout tags/v3.0
$ copy odm\examples\odm130.XML ..\input-ODMs\test.xml
$ mvn test
$ mvn exec:java -Dexec.mainClass="com.recomdata.i2b2.I2B2ODMStudyHandlerCMLClient" -Dexec.args="C:\path\to\workspace\ODM-to-i2b2\input-ODMs\test.xml C:\path\to\workspace\ODM-to-i2b2\output-tabular-files"
$ cd ..\output-tabular-files
$ dir
```
