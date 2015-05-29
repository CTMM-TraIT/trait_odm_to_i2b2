Users information
================
This page is meant for users who want to install and use ODM-to-i2b2 in order to convert an ODM file to
three tabular files. The installation can be executed entirely via the command line and has been tested
on a Windows system on which Maven was installed already. The project is downloaded from GitHub on issuing
the 'git clone' and 'git checkout' commands, as recommended below. The installation process can be followed
via the screenshots. Users who want to use/test the very latest state of the tool should not issue the
'git checkout tags/v3.0' command.

Installation
------------
```sh
Windows-key + cmd.exe + enter
$ cd C:\path\to\workspace
$ mkdir ODM-to-i2b2
$ cd ODM-to-i2b2
$ mkdir input-ODMs
$ mkdir output-tabular-files
$ git clone https://github.com/CTMM-TraIT/trait_odm_to_i2b2.git
$ cd trait_odm_to_i2b2
$ git checkout tags/v3.0
$ cp odm\examples\odm130.XML ..\input-ODMs\test.xml
$ mvn test
$ mvn exec:java -Dexec.mainClass="com.recomdata.i2b2.I2B2ODMStudyHandlerCMLClient" -Dexec.args="C:\path\to\workspace\ODM-to-i2b2\input-ODMs\test.xml C:\path\to\workspace\ODM-to-i2b2\output-tabular-files"
$ cd ..\output-tabular-files
$ ls
find the three tabular files ..._clinical_data.txt, ..._columns.txt, and ..._word_map.txt
```

Screenshots
-----------
![Image start command prompt](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/start_command_prompt.png)

![Image first commands](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/first_commands.png)

![Image test build success](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/test_build_success.png)

![Image execution success](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/execution_success.png)

![Image find the files](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/find_the_files.png)

![Image Excel view](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/Excel_view.png)

