package tests;

import api.OrderApi;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utils.TestDataGenerator;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.notNullValue;

@Feature("Заказы")
@DisplayName("Тесты API для работы с заказами")
@RunWith(Parameterized.class)
public class OrderTest {

    private OrderApi orderApi;
    private Integer createdOrderTrack;
    private final String[] colors;

    public OrderTest(String[] colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "Цвета: {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {new String[]{"BLACK"}},
            {new String[]{"GREY"}},
            {new String[]{"BLACK", "GREY"}},
            {new String[]{}}
        });
    }

    @Before
    public void setUp() {
        orderApi = new OrderApi();
    }

    @After
    public void tearDown() {
        if (createdOrderTrack != null) {
            try {
                orderApi.cancelOrder(createdOrderTrack);
                System.out.println("Попытка отмены заказа с треком: " + createdOrderTrack);
            } catch (Exception e) {
                System.out.println("Не удалось отменить заказ " + createdOrderTrack + ": " + e.getMessage());
            }
        }
    }

    // Параметризованный тест - запустится 4 раза
    @Test
    @Story("Создание заказа")
    @DisplayName("Создание заказа с разными цветами (параметризованный)")
    @Description("Проверка создания заказа с различными комбинациями цветов")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateOrderWithColors() {
        Order order = TestDataGenerator.generateOrderWithColors(colors);
        
        Response response = orderApi.createOrder(order);
        createdOrderTrack = response.then()
                .statusCode(201)
                .body("track", notNullValue())
                .extract()
                .path("track");
        
        System.out.println("✅ Создан заказ с треком: " + createdOrderTrack + " и цветами: " + Arrays.toString(colors));
    }

    // Отдельные тесты для каждого варианта (если нужно)
    @Test
    @Story("Создание заказа")
    @DisplayName("Создание заказа с черным цветом")
    @Description("Проверка создания заказа с черным цветом")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateOrderBlackOnly() {
        Order order = TestDataGenerator.generateOrderWithColors(new String[]{"BLACK"});
        
        Response response = orderApi.createOrder(order);
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Test
    @Story("Создание заказа")
    @DisplayName("Создание заказа с серым цветом")
    @Description("Проверка создания заказа с серым цветом")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateOrderGreyOnly() {
        Order order = TestDataGenerator.generateOrderWithColors(new String[]{"GREY"});
        
        Response response = orderApi.createOrder(order);
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Test
    @Story("Создание заказа")
    @DisplayName("Создание заказа с обоими цветами")
    @Description("Проверка создания заказа с черным и серым цветами")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateOrderBothColors() {
        Order order = TestDataGenerator.generateOrderWithColors(new String[]{"BLACK", "GREY"});
        
        Response response = orderApi.createOrder(order);
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Test
    @Story("Создание заказа")
    @DisplayName("Создание заказа без цвета")
    @Description("Проверка создания заказа без указания цвета")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateOrderNoColor() {
        Order order = TestDataGenerator.generateOrderWithColors(new String[]{});
        
        Response response = orderApi.createOrder(order);
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Test
    @Story("Получение заказов")
    @DisplayName("Получение списка заказов")
    @Description("Проверка получения списка заказов")
    @Severity(SeverityLevel.NORMAL)
    public void testGetOrdersList() {
        Response response = orderApi.getOrders();
        response.then()
                .statusCode(200)
                .body("orders", notNullValue());
        System.out.println("✅ Получен список заказов");
    }
}
