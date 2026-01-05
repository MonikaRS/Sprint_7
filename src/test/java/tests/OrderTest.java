package tests;

import api.OrderApi;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.hamcrest.Matchers.*;

@RunWith(Parameterized.class)
public class OrderTest {

    private final String[] color;
    private OrderApi orderApi;
    private Integer createdOrderTrack; // Трек созданного заказа для cleanup

    public OrderTest(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Цвет: {0}")
    public static Object[][] getColorData() {
        return new Object[][] {
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{}}
        };
    }

    @Before
    public void setUp() {
        orderApi = new OrderApi();
        createdOrderTrack = null;
    }

    @After
    public void tearDown() {
        // Отменяем созданный заказ после каждого теста
        if (createdOrderTrack != null) {
            try {
                orderApi.cancelOrder(createdOrderTrack)
                        .then()
                        .statusCode(200);
                System.out.println("Заказ с треком " + createdOrderTrack + " отменен");
            } catch (Exception e) {
                System.out.println("Не удалось отменить заказ с треком " + createdOrderTrack);
            }
        }
    }

    @Test
    @DisplayName("Создание заказа с разными вариантами цветов")
    @Description("Параметризованный тест: проверяем создание заказа с цветами BLACK, GREY, обоими цветами и без цвета")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateOrderWithDifferentColors() {
        Order order = new Order(
                "Тест",
                "Тестов",
                "Москва, Красная площадь 1",
                "4",
                "+79998887766",
                5,
                "2024-06-06",
                "Позвонить за час",
                color
        );

        Response response = orderApi.createOrder(order);

        response.then()
                .statusCode(201)
                .body("track", notNullValue());

        // Сохраняем трек для отмены в @After
        createdOrderTrack = response.jsonPath().getInt("track");
        System.out.println("Создан заказ с треком: " + createdOrderTrack + ", цвета: " +
                (color.length == 0 ? "нет" : String.join(", ", color)));
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка, что в тело ответа возвращается список заказов")
    @Severity(SeverityLevel.NORMAL)
    public void testGetOrdersList() {
        Response response = orderApi.getOrdersList();

        response.then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders", instanceOf(List.class))
                .body("pageInfo", notNullValue())
                .body("availableStations", notNullValue());

        int orderCount = response.jsonPath().getList("orders").size();
        System.out.println("Получено заказов: " + orderCount);
    }

    @Test
    @DisplayName("Получение заказа по трек-номеру")
    @Description("Проверка получения информации о заказе по его трек-номеру")
    @Severity(SeverityLevel.NORMAL)
    public void testGetOrderByTrack() {
        // Сначала создаем заказ
        Order order = new Order(
                "Анна",
                "Иванова",
                "Москва, ул. Тверская 10",
                "1",
                "+79991112233",
                3,
                "2024-06-10",
                "Не звонить в дверь",
                new String[]{"BLACK"}
        );

        Response createResponse = orderApi.createOrder(order);
        createdOrderTrack = createResponse.jsonPath().getInt("track");

        // Получаем заказ по треку
        Response trackResponse = orderApi.getOrderByTrack(createdOrderTrack);

        trackResponse.then()
                .statusCode(200)
                .body("order", notNullValue())
                .body("order.track", equalTo(createdOrderTrack))
                .body("order.firstName", equalTo("Анна"))
                .body("order.lastName", equalTo("Иванова"));
    }

    @Test
    @DisplayName("Получение списка заказов с параметрами")
    @Description("Проверка получения списка заказов с фильтрацией по параметрам")
    @Severity(SeverityLevel.NORMAL)
    public void testGetOrdersWithParameters() {
        Response response = orderApi.getOrdersListWithParams(
                null,           // courierId
                new String[]{"1", "2"}, // nearestStation
                10,             // limit
                0               // page
        );

        response.then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("pageInfo.limit", equalTo(10))
                .body("pageInfo.page", equalTo(0));
    }

    @Test
    @DisplayName("Попытка получить заказ по несуществующему треку")
    @Description("При запросе заказа по несуществующему треку должна возвращаться ошибка")
    @Severity(SeverityLevel.NORMAL)
    public void testGetOrderByNonExistentTrack() {
        int nonExistentTrack = 999999999;

        orderApi.getOrderByTrack(nonExistentTrack)
                .then()
                .statusCode(404)
                .body("message", notNullValue());
    }

    @Test
    @DisplayName("Отмена заказа")
    @Description("Проверка успешной отмены созданного заказа")
    @Severity(SeverityLevel.NORMAL)
    public void testCancelOrder() {
        // Создаем заказ
        Order order = new Order(
                "Петр",
                "Петров",
                "Санкт-Петербург, Невский пр. 50",
                "5",
                "+79992223344",
                2,
                "2024-06-15",
                "Оставить у двери",
                new String[]{"GREY"}
        );

        Response createResponse = orderApi.createOrder(order);
        int track = createResponse.jsonPath().getInt("track");

        // Отменяем заказ
        orderApi.cancelOrder(track)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));

        createdOrderTrack = null; // Не нужно отменять в @After
    }

    @Test
    @DisplayName("Попытка отменить несуществующий заказ")
    @Description("При отмене несуществующего заказа должна возвращаться ошибка")
    @Severity(SeverityLevel.NORMAL)
    public void testCancelNonExistentOrder() {
        int nonExistentTrack = 888888888;

        orderApi.cancelOrder(nonExistentTrack)
                .then()
                .statusCode(404)
                .body("message", notNullValue());
    }

    @Test
    @DisplayName("Проверка структуры ответа списка заказов")
    @Description("Подробная проверка структуры JSON ответа при получении списка заказов")
    @Severity(SeverityLevel.MINOR)
    public void testOrdersListStructure() {
        Response response = orderApi.getOrdersList();

        response.then()
                .statusCode(200)
                .body("orders[0].id", notNullValue())
                .body("orders[0].firstName", notNullValue())
                .body("orders[0].lastName", notNullValue())
                .body("orders[0].address", notNullValue())
                .body("orders[0].metroStation", notNullValue())
                .body("orders[0].phone", notNullValue())
                .body("orders[0].track", notNullValue())
                .body("pageInfo.page", notNullValue())
                .body("pageInfo.total", notNullValue())
                .body("pageInfo.limit", notNullValue())
                .body("availableStations[0].name", notNullValue())
                .body("availableStations[0].number", notNullValue())
                .body("availableStations[0].color", notNullValue());
    }
}