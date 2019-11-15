# java-selenium-cucumber

**Overview**
This repo consists of the automation scripts for the UI testing. 
Scripts were built using Java as a programming language + Cucumber, Selenium and Page Object Model structure.

**Info**
To exclude info/error logger from report - open configuration.properties and change the value of writeInfoLogsToReport/writeErrorLogsToReport to 'false'!

**Tools**
MousePos.exe(located under "src\main\recources\Tools") is used for locating X and Y mouse positions.

**Execution**
Sample Command to execute the tests from command line: mvn clean test -Dcucumber.options="--tags @TAGNAME"