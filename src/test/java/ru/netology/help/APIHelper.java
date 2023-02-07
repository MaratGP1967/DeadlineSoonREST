package ru.netology.help;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

public class APIHelper {
    private APIHelper() {
    }

    private static String token;
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setBasePath("/api")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static void shouldSetLogPass(DataHelper.AuthInfo userLog) {
        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(userLog)
                .when()
                .post("/auth")
                .then()
                .statusCode(200);
    }

    public static void shouldSetVerCode(DataHelper.AuthCode userCode) {
        Response response = given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .body(userCode)
                .when()
                .post("/auth/verification")
                .then()
                .statusCode(200)
                .extract()
                .response();
        token = response.path("token");
    }

    public static void shouldGetBalance(int sum_2, int sum_1) {
        given()
                .spec(requestSpec)
                .auth().oauth2(token)
                .when()
                .get("/cards")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("", hasSize(2))
                .body("[0].balance", equalTo(sum_2))
                .body("[1].balance", equalTo(sum_1));
    }

    public static void shouldChangeBalance(DataHelper.CardNumber fromTo) {
        given()
                .spec(requestSpec)
                .contentType(ContentType.JSON)
                .auth().oauth2(token)
                .body(fromTo)
                .when()
                .post("/transfer")
                .then()
                .statusCode(200);
    }
}
