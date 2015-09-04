Developers information
======================

This page is meant for developers who want to change something about the ODM-to-i2b2 tool, send a patch with a bug
fix or other improvement, or are interested in finding out more about the development process in general.

The project uses Java SE (Standard Edition) 7 and Maven 3. It was developed and tested on Windows and tested on
Linux/Unix. It is supposed to work on OS10 as well. It does not use databases nor webservers. It does not require
Java Enterprise Edition. The conversion tool only executes file operations. It parses the input ODM/XML file and
returns as output three tabular text files.

The installation can be executed entirely via the command line and has been tested
on Ubuntu Linux and on a Windows system. The project is downloaded from GitHub on issuing
the 'git clone' and 'git checkout' commands, as recommended below. The installation process can be followed
via the screenshots. Developers who want to use/test the very latest state of the tool should not issue the
'git checkout tags/v3.0' command.

Requirements:
-------------
- git
```sh
$ sudo apt-get install git
```
- Java 7 or Java 8
```sh
$ sudo add-apt-repository ppa:webupd8team/java
$ sudo apt-get update
$ sudo apt-get install oracle-java8-installer
```
- Maven 3
```sh
$ sudo apt-get install maven
```

Installation
------------
Linux:
```sh
$ cd /path/to/workspace
$ mkdir ODM-to-i2b2
$ cd ODM-to-i2b2
$ mkdir input-ODMs
$ mkdir output-tabular-files
$ git clone https://github.com/CTMM-TraIT/trait_odm_to_i2b2.git
$ cd trait_odm_to_i2b2
$ git checkout tags/v3.0
$ cp odm/examples/odm130.XML ../input-ODMs/test.xml
$ mvn test
$ mvn exec:java -Dexec.mainClass="com.recomdata.i2b2.I2B2ODMStudyHandlerCMLClient" -Dexec.args="/path/to/workspace/ODM-to-i2b2/input-ODMs/test.xml /path/to/workspace/ODM-to-i2b2/output-tabular-files"
$ cd ../output-tabular-files
$ ls -l
```

Screenshots
-----------
![Image first commands](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/first_commands.png)

![Image test build success](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/test_build_success.png)

![Image execution success](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/execution_success.png)

![Image find the files](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/find_the_files.png)

![Image Excel view](https://github.com/CTMM-TraIT/trait_odm_to_i2b2/blob/master/src/documentation/Excel_view.png)




We currently have some information on using git & GitHub (for code management) and Maven (for building the code). To
enhance and monitor the code quality, we use Checkstyle (code style checker), FindBugs (static code analysis tool),
PMD (another static code analysis tool), and CPD (copy/paste detector).


Using git and GitHub
--------------------

We are using [git](http://git-scm.com/) and [GitHub](https://github.com/) to manage the code of the ODM-to-i2b2
tool. Git is a free and open source distributed version control system, which makes it easy to work with a team on a
collection of (source code) files. GitHub is a web-based hosting service for software development projects that use
git (see [GitHub on Wikipedia](http://en.wikipedia.org/wiki/GitHub) for more information).

If you want to change the files in a GitHub repository, the common approach is to create a fork: a copy of the master
repository that the forked repository links to
(see [Fork A Repo on the GitHub web site](https://help.github.com/articles/fork-a-repo)). The most important command to
run after you have forked & cloned the repository:<br/>
**`git remote add upstream https://github.com/CTMM-TraIT/trait_odm_to_i2b2.git`**

Once you have created your own fork, you can commit and push changes to that repository. Then you can create a pull
request asking the maintainers of the master repository to accept your changes.

Changes that are made to the master repository by your team members, do not show up automatically in your forked
repository. You can fetch and merge their work using the following commands (on the command-line):

\# Fetch any new changes from the original master repository:<br/>
**`git fetch upstream`**

\# Merge any changes fetched into your working files:<br/>
**`git merge upstream/master`**

Merging sometimes leads to conflicts, which gives a message like this:<br/>
```
Updating 123a456..c789e00
error: Your local changes to the following files would be overwritten by merge:
        src/main/java/nl/vumc/odmtoi2b2/export/[SomeFile].java
Please, commit your changes or stash them before you can merge.
Aborting
```

Stashing your local changes is very easy:<br/>
**`git stash`**<br/>
```
Saved working directory and index state WIP on master: 123a456 [Some description.]
HEAD is now at 123a456 [Some description.]
```

If you want to remove all you local changes to files (tracked by git), you can use the reset command:
**`git reset --hard`**<br/>
```
HEAD is now at 123a456 [Some description.]
```

<!--- todo: add some information on branches: see http://genomewiki.ucsc.edu/index.php/Working_with_branches_in_Git -->


Maven
-----

We use [Maven](http://maven.apache.org/) as our build tool. This makes it easier to use certain tools (like Checkstyle
and FindBugs) and to manage the third-party libraries (dependencies) we use. The pom.xml file that Maven needs
is stored in the root directory: trait_odm_to_i2b2. All the maven commands need to be executed in this root directory.

Some commonly used Maven commands are
(see [Introduction to the Build Lifecycle](http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)
for an explanation of the build lifecycle and the build phases):

\# Remove all files generated by the previous build:<br/>
**`mvn clean`**

\# Compile the source code of the project:<br/>
**`mvn compile`**

\# Run all unit tests and generate code coverage report (in target\site\code-coverage-jacoco):<br/>
**`mvn test`**

\# Run a single unit test:<br/>
**`mvn -Dtest=OdmToFilesConverterTest test`**

\# Run the main class:<br/>
**`mvn exec:java -Dexec.mainClass="com.recomdata.i2b2.I2B2ODMStudyHandlerCMLClient"`**

\# Take the compiled code and package it in the odm-to-i2b2-x.y.z.jar file (in the target directory):<br/>
**`mvn package`**

\# Install the package into the local repository, for use as a dependency in other projects locally:<br/>
**`mvn install`**

\# Check for Checkstyle issues (report in target\checkstyle-result.xml):<br/>
**`mvn checkstyle:checkstyle`**

\# Check for FindBugs issues (report in target\findbugsXml.xml):<br/>
**`mvn compile findbugs:check`**

\# Check for PMD issues (report in target\pmd.xml):<br/>
**`mvn compile pmd:check`**

\# Check for CPD issues (report in target\cpd.xml):<br/>
**`mvn compile pmd:cpd-check`**

\# Create the jar file in the trait_odm_to_i2b2\target directory:<br/>
**`mvn clean compile assembly:single`**

Auto-generating the java files from the ODM-specification
---------------------------------------------------------
Java classes are generated for handling ODM files using the JAXB (Java Architecture for XML Binding) related xjc binding
compiler. The source code of these ODM reader classes are created by the xjc tool with xsd files that describe the ODM
standard as input. See for example https://jaxb.java.net/2.2.11/docs/ch04.html#tools-xjc or
https://en.wikipedia.org/wiki/Java_Architecture_for_XML_Binding for more information on xjc and JAXB.

The main schema file (ODM1-3-1.xsd) depends on several other schema files: ODM1-3-1-foundation.xsd, xml.xsd, and
xmldsig-core-schema.xsd. The bindings file (bindings.xml) modifies the default package names that are used. The Java
classes are put in two packages: org.cdisk.odm.jaxb (ODM1-3-1.xsd) and org.w3.xmldsig.jaxb (xmldsig-core-schema.xsd).

The call to xjc looks something like this:<br/>
**`xjc [schema file] -b [bindings file] -d [destination directory]`**

For example, on the Windows platform (with xjc.bat in the jaxb-ri\bin directory at your install location) using ODM 1.3.1,
you could generate the ODM reader classes like this:<br/>
**`mkdir java-generated`**<br/>
**`xjc.bat xsd\cdisc-odm-1.3.1\ODM1-3-1.xsd -b xsd\cdisc-odm-1.3.1\bindings.xml -d java-generated`**


Checkstyle
----------

We use Checkstyle to check for code style issues. Please check your code before committing. As we already mentioned in
the Maven section, you can run **`mvn checkstyle:checkstyle`** to run Checkstyle on the code (you can run this command
in the main project directory, which contains the pom.xml file Maven needs). The report is generated in the target
directory and is named checkstyle-result.xml.

It is also possible to configure a Java IDE (like Eclipse, IntelliJ or NetBeans) to integrate Checkstyle in your coding.


FindBugs
--------

FindBugs is a static code analysis tool that looks for constructs that might indicate bugs. It works by analyzing Java
bytecode (compiled class files), so compile the code before running FindBugs. If you run
**`mvn compile findbugs:check`** in the main project directory, FindBugs will print all its warnings and generate the
findbugsXml.xml file in the target directory.


PMD
---

PMD ("Programming Mistake Detector") is a static code analysis tool that looks for constructs that might indicate bugs
(like unused variables, parameters & private methods, empty blocks, overcomplicated expressions, and complex code). It
works by analyzing Java code. If you run **`mvn pmd:check`** in the main project directory, PMD will generate the
pmd.xml file in the target directory.


CPD
---

CPD (Copy/Paste Detector) is an add-on to PMD that searches for duplicated code (copied/pasted code can mean
copied/pasted bugs, and decreases maintainability). If you run **`mvn pmd:cpd-check`** in the main project directory,
CPD will generate the cpd.xml file in the target directory.
