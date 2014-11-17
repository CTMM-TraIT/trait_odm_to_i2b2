@rem ******
@rem * Copyright 2014 VU University Medical Center.
@rem * Licensed under the Apache License version 2.0 (see http://www.apache.org/licenses/LICENSE-2.0.html).
@rem *
@rem *
@rem * This batch file can be used to run a number of checks (on Windows):
@rem * unit tests, code coverage, Checkstyle, FindBugs, PMD, and CPD.
@rem *
@rem * author: Freek de Bruijn (f.debruijn@vumc.nl)
@rem ******


@echo Running checks for ODM-to-i2b2...

@echo.
@echo ============================
@echo Clean the project
@call mvn clean

@echo.
@echo ============================
@echo Run all unit tests
@pause
@call mvn test

@echo.
@echo ============================
@echo Check for Checkstyle issues (report in target\checkstyle-result.xml)
@pause
@call mvn checkstyle:checkstyle
@if not "%ERRORLEVEL%" == "0" less target/checkstyle-result.xml

@echo.
@echo ============================
@echo Check for FindBugs issues (report in target\findbugsXml.xml)
@pause
@call mvn compile findbugs:check
@if not "%ERRORLEVEL%" == "0" less target/findbugsXml.xml
@call copy target\findbugsXml.xml tmp

@rem * @echo.
@rem * @echo ============================
@rem * @echo Check for PMD issues (report in target\pmd.xml)
@rem * @pause
@rem * @call mvn compile pmd:check
@rem * @if not "%ERRORLEVEL%" == "0" less target/pmd.xml
@rem * 
@rem * @echo.
@rem * @echo ============================
@rem * @echo Check for CPD issues (report in target\cpd.xml)
@rem * @pause
@rem * @call mvn compile pmd:cpd-check
@rem * @if not "%ERRORLEVEL%" == "0" less target/cpd.xml