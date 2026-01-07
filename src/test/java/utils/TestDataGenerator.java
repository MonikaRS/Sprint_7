package utils;

import models.Order;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class TestDataGenerator {
    private static final Random random = new Random();

    public static String generateRandomLogin() {
        return "courier_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
    }

    public static String generateRandomPassword() {
        return "password_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
    }

    public static String generateRandomFirstName() {
        return "firstName_" + System.currentTimeMillis() + "_" + random.nextInt(10000);
    }

    // Единственный используемый метод для генерации заказа
    public static Order generateOrderWithColors(String[] colors) {
        return new Order(
                "Иван_" + random.nextInt(10000),
                "Иванов_" + random.nextInt(10000),
                "Москва, ул. Пушкина, дом " + random.nextInt(100),
                String.valueOf(random.nextInt(10) + 1),
                "+7999" + (1000000 + random.nextInt(9000000)),
                3 + random.nextInt(7),
                LocalDate.now().plusDays(3 + random.nextInt(7))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                "Тестовый заказ " + random.nextInt(10000),
                colors
        );
    }
}
