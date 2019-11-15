# ESIP-QA 

The purpose of this project is to validate that replication occurs between 2 oracle databases that are synchronized 
using SymmetricDS.  

<u>EsipDemoQA</u> 

This class contains the JUnit test that will post a patient's data (randomized by Java-faker API) to the 
Patient table, then validates that the file was posted by id.  After this, a call is made to validate
that the data synchronized to the database.  

In order to run the test, please run: 

mvn verify

This will generate the logs in the console.  
