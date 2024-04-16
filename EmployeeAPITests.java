import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class EmployeeAPITests {

    static String baseUrl = System.getenv("BASE_URL");
    String dbConnectionString = System.getenv("DB_CONNECTION_STRING");


    @BeforeAll
    static void setup() {
        RestAssured.baseURI = baseUrl;;
    }

    @Test
    void getEmployeesTest() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/employee")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @Test
    void addEmployeeTest() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "John Doe");
        requestBody.put("email", "john.doe@example.com");
        requestBody.put("phone", "1234567890");
        requestBody.put("companyId", 1);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/employee")
                .then()
                .statusCode(201)
                .extract().response();

        int employeeId = response.jsonPath().getInt("id");

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/employee/" + employeeId)
                .then()
                .statusCode(200)
                .body("name", equalTo("John Doe"))
                .body("email", equalTo("john.doe@example.com"))
                .body("phone", equalTo("1234567890"))
                .body("companyId", equalTo(1));
    }

    @Test
    void updateEmployeeTest() {
        // Создаем тело запроса для обновления информации о сотруднике
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", "Updated Name");

        // Отправляем PATCH-запрос на /employee/{id} и проверяем успешность запроса
        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .patch("/employee/{id}", 1)
                .then()
                .statusCode(200);

        // Отправляем GET-запрос на /employee/{id} и проверяем обновленные данные о сотруднике
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/employee/1")
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Name"));
    }

    @Test
    void getEmployeeByIdTest() {
        // Отправляем GET-запрос на /employee/{id} и проверяем успешность запроса и данные о сотруднике
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/employee/{id}", 1)
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }
}

