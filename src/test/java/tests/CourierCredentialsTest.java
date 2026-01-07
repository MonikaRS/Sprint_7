package tests;

import api.CourierApi;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Courier;
import models.CourierCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.TestDataGenerator;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

@Feature("Курьеры")
@DisplayName("Тесты API для работы с курьерами (с использованием CourierCredentials)")
public class CourierCredentialsTest {

    private CourierApi courierApi;
    private Integer courierId;

    @Before
    public void setUp() {
        courierApi = new CourierApi();
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            try {
                Response deleteResponse = courierApi.deleteCourier(courierId);
                deleteResponse.then().statusCode(200);
                System.out.println("✓ Курьер с ID " + courierId + " удален");
            } catch (Exception e) {
                System.out.println("⚠️ Не удалось удалить курьера: " + e.getMessage());
            }
        }
    }

    @Test
    @Story("Авторизация курьера")
    @DisplayName("Успешная авторизация с использованием CourierCredentials")
    @Description("Проверка что CourierCredentials корректно работает для логина")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginWithCourierCredentials() {
        // 1. Создаем курьера
        String login = TestDataGenerator.generateRandomLogin();
        String password = TestDataGenerator.generateRandomPassword();
        Courier courier = new Courier(
                login,
                password,
                TestDataGenerator.generateRandomFirstName()
        );

        courierApi.createCourier(courier)
                .then()
                .statusCode(201);

        // 2. Логинимся с CourierCredentials
        CourierCredentials credentials = new CourierCredentials(login, password);
        Response loginResponse = courierApi.loginCourier(credentials);
        
        courierId = loginResponse.then()
                .statusCode(200)
                .body("id", notNullValue())
                .extract()
                .path("id");
    }

    @Test
    @Story("Авторизация курьера")
    @DisplayName("Авторизация с неверным паролем")
    @Description("Проверка ошибки при авторизации с неверным паролем")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithWrongPassword() {
        // 1. Создаем курьера
        String login = TestDataGenerator.generateRandomLogin();
        String password = TestDataGenerator.generateRandomPassword();
        Courier courier = new Courier(
                login,
                password,
                TestDataGenerator.generateRandomFirstName()
        );

        courierApi.createCourier(courier)
                .then()
                .statusCode(201);

        // 2. Пытаемся залогиниться с неверным паролем
        CourierCredentials wrongCredentials = new CourierCredentials(login, "wrong_password");
        courierApi.loginCourier(wrongCredentials)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));

        // 3. Залогиниться с правильными данными для удаления
        CourierCredentials correctCredentials = new CourierCredentials(login, password);
        Response loginResponse = courierApi.loginCourier(correctCredentials);
        courierId = loginResponse.then()
                .statusCode(200)
                .extract()
                .path("id");
    }

    @Test
    @Story("Авторизация курьера")
    @DisplayName("Авторизация без логина")
    @Description("Проверка ошибки при авторизации без логина")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginWithoutLogin() {
        CourierCredentials credentials = new CourierCredentials("", "password");
        courierApi.loginCourier(credentials)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Story("Авторизация курьера")
    @DisplayName("Авторизация несуществующего курьера")
    @Description("Проверка ошибки при авторизации несуществующего курьера")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginNonExistentCourier() {
        CourierCredentials credentials = new CourierCredentials(
                "nonexistent_" + System.currentTimeMillis(),
                "password"
        );
        courierApi.loginCourier(credentials)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}
