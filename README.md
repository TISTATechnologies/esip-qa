# ESIP-QA 
<<<<<<< HEAD

The purpose of this project is to validate that replication occurs between 2 oracle databases that are synchronized 
using SymmetricDS.  

<u>EsipDemoQA</u> 

This class contains the JUnit test that will post a patient's data (randomized by Java-faker API) to the 
Patient table, then validates that the file was posted by newly generated id.  After this, a call is made to validate
that the data synchronized to the second database.  

In order to run the test, go to the esip-qa directory where the pom file is located, and execute the following command: 

mvn verify

This will run the test and generate the logs in the console.  






=======

The purpose of this project is to validate that replication occurs between 2 oracle databases that are synchronized 
using SymmetricDS.  

<u>EsipDemoQA</u> 

This class contains the JUnit test that will post a patient's data (randomized by Java-faker API) to the 
Patient table, then validates that the file was posted by id.  After this, a call is made to validate
that the data synchronized to the database.  

In order to run the test, please run: 

mvn verify

This will generate the logs in the console.  
>>>>>>> b80c364a85b9822845d400efee8c8d958e299180
