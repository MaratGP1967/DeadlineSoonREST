package ru.netology.rest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.netology.help.DBHelper;
import ru.netology.help.User;
import java.sql.DriverManager;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BackendSQLTest {
    private static String token;
    private RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setBasePath("/api")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    @AfterAll
    @SneakyThrows
    static void cleanUp() {
        var runner = new QueryRunner();
        var auth_codesSQL = "DELETE FROM auth_codes;";
        var cardsSQL = "DELETE FROM cards;";
        var usersSQL = "DELETE FROM users;";

        try (
                var conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                )
        ) {
            runner.update(conn, auth_codesSQL);
            runner.update(conn, cardsSQL);
            runner.update(conn, usersSQL);
        }
    }

    @Test
    void shouldWork() {
        String userLog = "{\"login\": \"vasya\",\"password\": \"qwerty123\"}";
        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(userLog)
                .when()
                .post("/auth")
                .then()
                .statusCode(200)
        ;
        User userCode = new User("vasya", DBHelper.getAuthCode("vasya"));
        Response response = given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(userCode)
                .when()
                .post("/auth/verification")
                .then()
                .statusCode(200)
                .extract()
                .response()
                ;
        token = response.path("token");
        given()
                .spec(requestSpec)
                .auth().oauth2(token)
                .when()
                .get("/cards")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(2))
                .body("[0].id", equalTo("0f3f5c2a-249e-4c3d-8287-09f7a039391d"))
                .body("[0].number", equalTo("**** **** **** 0002"))
                .body("[0].balance", equalTo(10000))
                .body("[1].id", equalTo("92df3f1c-a033-48e6-8390-206f6b1f56c0"))
                .body("[1].number", equalTo("**** **** **** 0001"))
                .body("[1].balance", equalTo(10000))
        ;
        String user = "{\"from\": \"5559 0000 0000 0002\", \"to\": \"5559 0000 0000 0001\", \"amount\": 5000}";
        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(user)
                .when()
                .post("/transfer")
                .then()
                .statusCode(200)
        ;
        given()
                .spec(requestSpec)
                .auth().oauth2(token)
                .when()
                .get("/cards")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(2))
                .body("[0].id", equalTo("0f3f5c2a-249e-4c3d-8287-09f7a039391d"))
                .body("[0].number", equalTo("**** **** **** 0002"))
                .body("[0].balance", equalTo(5000))
                .body("[1].id", equalTo("92df3f1c-a033-48e6-8390-206f6b1f56c0"))
                .body("[1].number", equalTo("**** **** **** 0001"))
                .body("[1].balance", equalTo(15000))
        ;
        String user1 = "{\"from\": \"5559 0000 0000 0002\", \"to\": \"5559 0000 0000 0001\", \"amount\": -5000}";
        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(user1)
                .when()
                .post("/transfer")
                .then()
                .statusCode(200)
        ;
    }
}
