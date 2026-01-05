package tests;

import api.CourierApi;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Courier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class CourierTest {

    private CourierApi courierApi;
    private Courier testCourier;
    private String courierId;

    @Before
    public void setUp() {
        courierApi = new CourierApi();
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            courierApi.deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Проверка, что курьера можно создать с валидными данными")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateCourierSuccess() {
        testCourier = new Courier("courier_" + System.currentTimeMillis(),
                "password123", "Test Courier");

        courierApi.createCourier(testCourier)
                .then()
                .statusCode(201)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    @Description("По документации: нельзя создать двух курьеров с одинаковым логином. Фактически: возвращает 409 с сообщением 'Этот логин уже используется. Попробуйте другой.'")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateDuplicateCourier() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        testCourier = new Courier("duplicate_" + timestamp, "pass123", "Duplicate");

        courierApi.createCourier(testCourier)
                .then()
                .statusCode(201);

        // По документации ожидается сообщение "Этот логин уже используется"
        // Фактически API возвращает "Этот логин уже используется. Попробуйте другой."
        courierApi.createCourier(testCourier)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется"));

        // Очистка
        Response loginResponse = courierApi.loginCourier(testCourier);
        courierId = loginResponse.jsonPath().getString("id");
    }

    @Test
    @DisplayName("Создание курьера без логина")
    @Description("По документации: при создании курьера без логина должна возвращаться ошибка 400")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateCourierWithoutLogin() {
        testCourier = new Courier(null, "password123", "No Login");

        courierApi.createCourier(testCourier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    @Description("По документации: при создании курьера без пароля должна возвращаться ошибка 400")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateCourierWithoutPassword() {
        testCourier = new Courier("nopass_" + System.currentTimeMillis(),
                null, "No Password");

        courierApi.createCourier(testCourier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    @Description("Курьер может авторизоваться с правильными данными")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginCourierSuccess() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        testCourier = new Courier("login_" + timestamp, "pass123", "Login Test");

        courierApi.createCourier(testCourier)
                .then()
                .statusCode(201);

        Response response = courierApi.loginCourier(testCourier);
        response.then()
                .statusCode(200)
                .body("id", notNullValue());

        courierId = response.jsonPath().getString("id");
    }

    @Test
    @DisplayName("Авторизация с неправильным паролем")
    @Description("При авторизации с неправильным паролем должна быть ошибка 404")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginWithWrongPassword() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        testCourier = new Courier("wrongpass_" + timestamp, "correct123", "Wrong Pass");

        courierApi.createCourier(testCourier)
                .then()
                .statusCode(201);

        Courier wrongPassCourier = new Courier(testCourier.getLogin(), "wrongpassword", null);

        courierApi.loginCourier(wrongPassCourier)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));

        // Очистка
        Response loginResponse = courierApi.loginCourier(testCourier);
        courierId = loginResponse.jsonPath().getString("id");
    }

    @Test
    @DisplayName("Авторизация с неправильным логином")
    @Description("При авторизации с неправильным логином должна быть ошибка 404")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginWithWrongLogin() {
        Courier nonExistentCourier = new Courier("nonexistent_" + System.currentTimeMillis(),
                "password123", null);

        courierApi.loginCourier(nonExistentCourier)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Авторизация без логина")
    @Description("По документации: при авторизации без логина должна быть ошибка 400")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginWithoutLogin() {
        Courier noLoginCourier = new Courier(null, "password123", null);

        courierApi.loginCourier(noLoginCourier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Авторизация без пароля")
    @Description("По документации: ожидается 400. Фактически: API возвращает 504 Gateway Timeout")
    @Severity(SeverityLevel.CRITICAL)
    public void testLoginWithoutPassword() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        testCourier = new Courier("nopasslogin_" + timestamp, "password123", "Test");

        courierApi.createCourier(testCourier)
                .then()
                .statusCode(201);

        Courier noPasswordCourier = new Courier(testCourier.getLogin(), null, null);

        // По документации ожидается 400, но API возвращает 504
        // Тест упадет, фиксируя несоответствие API документации
        courierApi.loginCourier(noPasswordCourier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));

        // Очистка
        Response loginResponse = courierApi.loginCourier(testCourier);
        courierId = loginResponse.jsonPath().getString("id");
    }

    @Test
    @DisplayName("Авторизация несуществующего курьера")
    @Description("При авторизации несуществующего курьера должна быть ошибка 404")
    @Severity(SeverityLevel.NORMAL)
    public void testLoginNonExistentCourier() {
        Courier fakeCourier = new Courier("fake_" + System.currentTimeMillis(),
                "fakepass", null);

        courierApi.loginCourier(fakeCourier)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }
}