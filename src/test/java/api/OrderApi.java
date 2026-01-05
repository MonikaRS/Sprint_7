package api;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import models.Order;

import static io.restassured.RestAssured.given;

public class OrderApi {

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("https://qa-scooter.praktikum-services.ru")
            .setContentType(ContentType.JSON)
            .build();

    public Response createOrder(Order order) {
        return given()
                .spec(requestSpec)
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    public Response getOrdersList() {
        return given()
                .spec(requestSpec)
                .when()
                .get("/api/v1/orders");
    }

    public Response getOrdersListWithParams(Integer courierId, String[] nearestStation,
                                            Integer limit, Integer page) {
        return given()
                .spec(requestSpec)
                .queryParam("courierId", courierId)
                .queryParam("nearestStation", nearestStation)
                .queryParam("limit", limit)
                .queryParam("page", page)
                .when()
                .get("/api/v1/orders");
    }

    public Response getOrderByTrack(int track) {
        return given()
                .spec(requestSpec)
                .queryParam("t", track)
                .when()
                .get("/api/v1/orders/track");
    }

    public Response acceptOrder(int orderId, int courierId) {
        return given()
                .spec(requestSpec)
                .queryParam("courierId", courierId)
                .when()
                .put("/api/v1/orders/accept/" + orderId);
    }

    public Response finishOrder(int orderId) {
        return given()
                .spec(requestSpec)
                .when()
                .put("/api/v1/orders/finish/" + orderId);
    }

    public Response cancelOrder(int track) {
        return given()
                .spec(requestSpec)
                .body("{\"track\": " + track + "}")
                .when()
                .put("/api/v1/orders/cancel");
    }

    public Response getCourierOrdersCount(int courierId) {
        return given()
                .spec(requestSpec)
                .when()
                .get("/api/v1/courier/" + courierId + "/ordersCount");
    }
}