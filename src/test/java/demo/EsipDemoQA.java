package demo;

import com.github.javafaker.Faker;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class EsipDemoQA {
  Header header = new Header("Content-Type", "application/json");
  static String patientID = "";
  static String firstName = "";
  static String lastName = "";
  static String middleName = "";
  static String dateOfBirth = "";

  @Test
  public void test1createPatientMicroservices() {
    String log4j2ConfigFile = "./Properties/log4j2.xml";
    String API_SOURCE_SERVER_HOST = "https://wpf9abnloj.execute-api.us-east-1.amazonaws.com/test/";
    String API_TARGET_CLIENT_HOST = "https://eko2nk4gfl.execute-api.us-east-1.amazonaws.com/test/";
    String ENDPOINT_SOURCE_CREATE_PATIENT = API_SOURCE_SERVER_HOST + "patient/add";
    String ENDPOINT_SOURCE_GET_PATIENT = API_SOURCE_SERVER_HOST + "patient/get/";
    String ENDPOINT_TARGET_GET_PATIENT =
            "https://eko2nk4gfl.execute-api.us-east-1.amazonaws.com/test/patient/get/";
    Header header = new Header("Content-Type", "application/json");
    System.setProperty("log4j.configurationFile", log4j2ConfigFile);
    Logger logger = LogManager.getLogger(this.getClass());
    Faker faker = new Faker();
    Random random = new Random();
    long randomDay =
            (int) LocalDate.of(1900, 1, 1).toEpochDay()
                    + random.nextInt(
                    (int) LocalDate.of(2019, 1, 1).toEpochDay()
                            - (int) LocalDate.of(1900, 1, 1).toEpochDay());
    System.out.println("");
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("RUNNING THE MICROSERVICE TESTS");
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();
    middleName = faker.name().firstName();
    dateOfBirth = LocalDate.ofEpochDay(randomDay).toString();
    logger.info("Generated First Name is: " + firstName);
    logger.info("Generated Last Name is: " + lastName);
    logger.info("Generated Middle Name is: " + middleName);
    logger.info("Generated Date Of Birth is: " + dateOfBirth);
    String email = firstName + lastName + "@mail.com";
    logger.info("Generated Email address is: " + email);
    String requestString =
            convertRequestToString("./src/test/java/jsonFiles/create_patient.json")
                    .replace("@@FirstName@@", firstName)
                    .replace("@@LastName@@", lastName)
                    .replace("@@MiddleName@@", middleName)
                    .replace("@@dateOfBirth@@", dateOfBirth)
                    .replace("@@email@@", email);
    logger.info("Attempting to connect to server https://wpf9abnloj.execute-api.us-east-1.amazonaws.com");
    logger.info("Creating a new Patient where Request body is: ");
    System.out.println(requestString);
    /*
     *
     * SERVER 1 POST REQUEST - POSTING A REQUEST RECORD.
     *
     * */

    Response response =
            given()
                    .header(header)
                    .and()
                    .body(requestString)
                    .when()
                    .post(ENDPOINT_SOURCE_CREATE_PATIENT);
    assertEquals(200, response.statusCode());
    logger.info("Successfully connected to Microservice Server | Status code is: 200");
    patientID = response.asString().split(" - ")[1];
    logger.info("New Patient has been successfully created with id: " + patientID);

    /*
     *
     * SERVER 1 GET REQUEST - RETRIEVING THE POST REQUEST RECORD.
     *
     * */
    logger.info("Attempting to connect to database #1: https://wpf9abnloj.execute-api.us-east-1.amazonaws.com");
    logger.info("Finding newly created Patient by it's id: " + patientID);
    response = given().header(header).when().get(ENDPOINT_SOURCE_GET_PATIENT + patientID);
    assertEquals(200, response.statusCode());
    logger.info("Successfully connected to Server | Status code is: " + response.statusCode());
    logger.info("Patient with id: " + patientID + " has below details:");
    System.out.println(response.asString());
    JsonPath jPath = new JsonPath(response.asString());
    logger.warn("First Name is equal to: " + jPath.getString("firstName"));
    logger.warn("Middle Name is equal to: " + jPath.getString("middleName"));
    logger.warn("Last Name is equal to: " + jPath.getString("lastName"));
    logger.warn("Date Of Birth is equal to: " + jPath.getString("dateOfBirth"));
    logger.info("Attempting to connect to database #2: https://eko2nk4gfl.execute-api.us-east-1.amazonaws.com");
    logger.info("Finding newly created Patient by it's id: " + patientID);
    try {
      Thread.sleep(15000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    /*
     *
     * SERVER 2 Verifying that tables are syncronized.
     *
     * */
    response = given().header(header).when().get(ENDPOINT_TARGET_GET_PATIENT + patientID);
    assertEquals(200, response.statusCode());
    logger.info("Successfully connected to Server | Status code is: " + response.statusCode());
    logger.info("Patient with id: " + patientID + " has below details:");
    System.out.println(response.asString());
    jPath = new JsonPath(response.asString());
    logger.warn("First Name is equal to: " + jPath.getString("firstName"));
    logger.warn("Middle Name is equal to: " + jPath.getString("middleName"));
    logger.warn("Last Name is equal to: " + jPath.getString("lastName"));
    logger.warn("Date Of Birth is equal to: " + jPath.getString("dateOfBirth"));
  }

  //@Test
  public void test2createPatientHapiFHIR() {
    String log4j2ConfigFile = "./Properties/log4j2.xml";
    String API_SOURCE_SERVER_HOST = "http://52.202.77.60:9090/esip/v1/";
    String API_TARGET_SERVER_HOST = "http://3.133.161.132:9090/esip/v1/";
    String ENDPOINT_SOURCE_CREATE_PATIENT = API_SOURCE_SERVER_HOST + "add/patient";
    String ENDPOINT_SOURCE_GET_PATIENT = API_SOURCE_SERVER_HOST + "get/patient/";
    String ENDPOINT_TARGET_GET_PATIENT = API_TARGET_SERVER_HOST + "get/patient/";
    System.setProperty("log4j.configurationFile", log4j2ConfigFile);
    Logger logger = LogManager.getLogger(this.getClass());
    Faker faker = new Faker();
    Random random = new Random();
    long randomDay =
        (int) LocalDate.of(1900, 1, 1).toEpochDay()
            + random.nextInt(
                (int) LocalDate.of(2019, 1, 1).toEpochDay()
                    - (int) LocalDate.of(1900, 1, 1).toEpochDay());
    System.out.println("RUNNING THE HAPI API TESTS");
    firstName = faker.name().firstName();
    lastName = faker.name().lastName();
    middleName = faker.name().firstName();
    dateOfBirth = LocalDate.ofEpochDay(randomDay).toString();
    logger.info("Generated First Name is: " + firstName);
    logger.info("Generated Last Name is: " + lastName);
    logger.info("Generated Middle Name is: " + middleName);
    logger.info("Generated Date Of Birth is: " + dateOfBirth);
    String email = firstName + lastName + "@mail.com";
    logger.info("Generated Email address is: " + email);
    String requestString =
        convertRequestToString("./src/test/java/jsonFiles/create_patient.json")
            .replace("@@FirstName@@", firstName)
            .replace("@@LastName@@", lastName)
            .replace("@@MiddleName@@", middleName)
            .replace("@@dateOfBirth@@", dateOfBirth)
            .replace("@@email@@", email);
    logger.info(
        "Attempting to connect to Hapi FHIR server and create a new Patient where Request body is: ");
    System.out.println(requestString);

    /*
     *
     * SERVER 1 POST REQUEST - POSTING A NEW PATIENT RECORD THROUGH HAPI API.
     *
     * */
    Response response =
        given()
            .header(header)
            .and()
            .body(requestString)
            .when()
            .post(ENDPOINT_SOURCE_CREATE_PATIENT);
    assertEquals(200, response.statusCode());
    logger.info("Successfully connected to HAPI Server | Status code is: 200");
    patientID = response.asString().split(" - ")[1];
    logger.info("New Patient has been successfully created with id: " + patientID);
    logger.info(
        "Attempting to connect to database #1 and find newly created Patient by it's id: "
            + patientID);
    /*
     *
     * SERVER 2 Verification SHOWING REPLICATION HAPI API
     *
     * */
    response = given().header(header).when().get(ENDPOINT_SOURCE_GET_PATIENT + patientID);
    assertEquals(200, response.statusCode());
    logger.info("Successfully connected to Server | Status code is: " + response.statusCode());
    logger.info("Patient with id: " + patientID + " has below details:");
    System.out.println(response.asString());
    JsonPath jPath = new JsonPath(response.asString());
    logger.warn("First Name is equal to: " + jPath.getString("firstName"));
    logger.warn("Middle Name is equal to: " + jPath.getString("middleName"));
    logger.warn("Last Name is equal to: " + jPath.getString("lastName"));
    logger.warn("Date Of Birth is equal to: " + jPath.getString("dateOfBirth"));
    /*
     *
     * SERVER 2 Verification SHOWING REPLICATION
     *
     * */
    logger.info(
        "Attempting to connect to database #2 and find newly created Patient by it's id: "
            + patientID);
    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    response = given().header(header).when().get(ENDPOINT_TARGET_GET_PATIENT + patientID);
    assertEquals(200, response.statusCode());
    logger.info("Successfully connected to Server | Status code is: " + response.statusCode());
    logger.info("Patient with id: " + patientID + " has below details:");
    System.out.println(response.asString());
    jPath = new JsonPath(response.asString());
    logger.warn("First Name is equal to: " + jPath.getString("firstName"));
    logger.warn("Middle Name is equal to: " + jPath.getString("middleName"));
    logger.warn("Last Name is equal to: " + jPath.getString("lastName"));
    logger.warn("Date Of Birth is equal to: " + jPath.getString("dateOfBirth"));
  }

  public static String convertRequestToString(String path) {
    String temp = null;
    try {
      temp = new String(Files.readAllBytes(Paths.get(path)));
    } catch (IOException e) {
    }
    return temp;
  }
}
