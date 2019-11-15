package demo;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import com.github.javafaker.Faker;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
public class EsipDemoQA {
    private static String log4j2ConfigFile = "./Properties/log4j2.xml";
    String PATIENT_CREATE_URL = "http://52.202.77.60:9090/esip/v1/add/patient";
    String GET_PATIENT_SERVER_URL = "http://52.202.77.60:9090/esip/v1/get/patient/";
    String GET_PATIENT_CLIENT_URL = "http://3.133.161.132:9090/esip/v1/get/patient/";
    Header header = new Header("Content-Type", "application/json");
    static String patientID = "";
    static String firstName = "";
    static String lastName = "";
    static String middleName = "";
    static String dateOfBirth = "";

    @Test
    public void test1createPatient() {
        System.setProperty("log4j.configurationFile", log4j2ConfigFile);
        Logger logger = LogManager.getLogger(this.getClass());
        Faker faker = new Faker();
        Random random = new Random();
        long randomDay = (int) LocalDate.of(1900, 1, 1).toEpochDay() + random.nextInt((int) LocalDate.of(2019, 1, 1).toEpochDay() - (int) LocalDate.of(1900, 1, 1).toEpochDay());
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
        String requestString = convertRequestToString("./src/test/java/jsonFiles/create_patient.json")
                .replace("@@FirstName@@", firstName).replace("@@LastName@@", lastName).replace("@@MiddleName@@", middleName)
                .replace("@@dateOfBirth@@", dateOfBirth).replace("@@email@@", email);
        logger.info("Attempting to connect to server and create a new Patient where Request body is: ");
        System.out.println(requestString );
        Response response = given().header(header)
                .and().body(requestString)
                .when().post(PATIENT_CREATE_URL);
        assertEquals(200, response.statusCode());
        logger.info("Successfully connected to Server | Status code is: 200");
        patientID = response.asString().split(" - ")[1];
        logger.info("New Patient has been successfully created with id: " + patientID);
        System.out.println("");
        logger.info("Attempting to connect to database #1 and find newly created Patient by it's id: " + patientID);
        response = given().header(header)
                .when().get(GET_PATIENT_SERVER_URL + patientID);
        assertEquals(200, response.statusCode());
        logger.info("Successfully connected to Server | Status code is: 200");
        logger.info("Patien with id: " + patientID + " has below deatails:");
        System.out.println("");
        System.out.println(response.asString());
        System.out.println("");
        JsonPath jPath = new JsonPath(response.asString());
        logger.warn("First Name is equal to: " + jPath.getString("firstName"));
        logger.warn("Middle Name is equal to: " + jPath.getString("middleName"));
        logger.warn("Last Name is equal to: " + jPath.getString("lastName"));
        logger.warn("Date Of Birth is equal to: " + jPath.getString("dateOfBirth"));
        System.out.println("");
//        logger.info("Attempting to connect to database #2 and find newly created Patient by it's id: " + patientID);
//        response = given().header(header)
//                .when().get(GET_PATIENT_CLIENT_URL + patientID);
//        assertEquals(200, response.statusCode());
//        logger.info("Successfully connected to Server | Status code is: 200");
//        logger.info("Patien with id: " + patientID + " has below deatails:");
//        System.out.println("");
//        System.out.println(response.asString());
//        System.out.println("");
//        jPath = new JsonPath(response.asString());
//        logger.warn("First Name is equal to: " + jPath.getString("firstName"));
//        logger.warn("Middle Name is equal to: " + jPath.getString("middleName"));
//        logger.warn("Last Name is equal to: " + jPath.getString("lastName"));
//        logger.warn("Date Of Birth is equal to: " + jPath.getString("dateOfBirth"));
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