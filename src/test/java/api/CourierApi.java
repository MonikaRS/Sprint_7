package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Courier;

import static io.restassured.RestAssured.given;

public class CourierApi {

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://qa-scooter.praktikum-services.ru")
            .setContentType(ContentType.JSON)
            .build();

    public Response createCourier(Courier courier) {
        return given()
                .spec(requestSpec)
                .body(courier)
                .when()
                .post("/api/v1/courier");
    }

    public Response loginCourier(Courier courier) {
        return given()
                .spec(requestSpec)
                .body(courier)
                .when()
                .post("/api/v1/courier/login");
    }

    public Response loginCourier(String login, String password) {
        return given()
                .spec(requestSpec)
                .body("{\"login\": \"" + login + "\", \"password\": \"" + password + "\"}")
                .when()
                .post("/api/v1/courier/login");
    }

    public Response deleteCourier(String id) {
        return given()
                .spec(requestSpec)
                .when()
                .delete("/api/v1/courier/" + id);
    }

    public Response deleteCourier(int id) {
        return deleteCourier(String.valueOf(id));
    }
}