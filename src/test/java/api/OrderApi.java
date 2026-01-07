package api;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Order;
import utils.Config;

import static io.restassured.RestAssured.given;

public class OrderApi {

    private static final RequestSpecification REQUEST_SPEC = new RequestSpecBuilder()
            .setBaseUri(Config.BASE_URL)
            .setBasePath(Config.BASE_PATH)
            .setContentType(ContentType.JSON)
            .addFilter(new AllureRestAssured())
            .build();

    @Step("Создание заказа")
    public Response createOrder(Order order) {
        return given()
                .spec(REQUEST_SPEC)
                .body(order)
                .when()
                .post(Config.ORDER);
    }

    @Step("Получение списка заказов")
    public Response getOrders() {
        return given()
                .spec(REQUEST_SPEC)
                .when()
                .get(Config.ORDER);
    }

    @Step("Отмена заказа с track: {track}")
    public Response cancelOrder(Integer track) {
        String cancelBody = String.format("{\"track\": %d}", track);
        return given()
                .spec(REQUEST_SPEC)
                .body(cancelBody)
                .when()
                .put(Config.ORDER + "/cancel");
    }
}
