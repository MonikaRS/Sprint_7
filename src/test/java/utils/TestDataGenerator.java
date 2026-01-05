package utils;

import java.util.Random;

public class TestDataGenerator {
    private static final Random random = new Random();
    
    public static String generateRandomLogin() {
        return "courier_" + random.nextInt(10000);
    }
    
    public static String generateRandomPassword() {
        return "password_" + random.nextInt(10000);
    }
    
    public static String generateRandomFirstName() {
        return "firstName_" + random.nextInt(10000);
    }
}
