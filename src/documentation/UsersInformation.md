Users information
================
This page is meant for users who want to install and use ODM-to-i2b2 in order to convert an ODM file to
three tabular files. Please follow the instructions below:

First download [odm-to-i2b2-3.0.zip](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/releases/odm-to-i2b2-3.0.zip?raw=true).

```
Save it in your workspace and unzip the file.
Save your ODM file in odm-to-i2b2-3.0\input-ODMs (e.g. odm130.XML)
Start the command line terminal (e.g. cmd.exe)
$ cd C:\path\to\odm-to-i2b2-3.0
$ java -jar odm-to-i2b2-3.0.jar input-ODMs\odm130.XML output-tabular-files
$ cd output-tabular-files
$ ls -l
```

![Image cmd execution](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/cmd_execution.png)
