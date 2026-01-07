package utils;

public class Config {
    public static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    public static final String BASE_PATH = "/api/v1"; // Используется в OrderApi
    
    // Пути для курьеров (OrderApi использует BASE_PATH + ORDER)
    public static final String COURIER = "/courier";
    public static final String COURIER_LOGIN = "/courier/login";
    
    // Пути для заказов
    public static final String ORDER = "/orders";
}
