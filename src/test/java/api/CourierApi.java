package api;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Courier;
import models.CourierCredentials;
import utils.Config;

import static io.restassured.RestAssured.given;

public class CourierApi {

    private static final RequestSpecification REQUEST_SPEC = new RequestSpecBuilder()
            .setBaseUri(Config.BASE_URL)
            .setBasePath(Config.BASE_PATH)
            .setContentType(ContentType.JSON)
            .addFilter(new AllureRestAssured())
            .build();

    @Step("Создание курьера")
    public Response createCourier(Courier courier) {
        return given()
                .spec(REQUEST_SPEC)
                .body(courier)
                .when()
                .post(Config.COURIER);
    }

    @Step("Логин курьера с логином: {courier.login}")
    public Response loginCourier(Courier courier) {
        CourierCredentials credentials = new CourierCredentials(courier.getLogin(), courier.getPassword());
        return loginCourier(credentials);
    }

    @Step("Логин курьера с учетными данными")
    public Response loginCourier(CourierCredentials credentials) {
        return given()
                .spec(REQUEST_SPEC)
                .body(credentials)
                .when()
                .post(Config.COURIER_LOGIN);
    }

    @Step("Удаление курьера с ID: {id}")
    public Response deleteCourier(Integer id) {
        return given()
                .spec(REQUEST_SPEC)
                .when()
                .delete(Config.COURIER + "/" + id);
    }
}
