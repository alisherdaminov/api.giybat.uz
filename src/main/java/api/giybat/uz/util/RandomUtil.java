package api.giybat.uz.util;

import java.util.Random;

public class RandomUtil {
    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    public static final Random random = new Random();

    public static String generateRandomNumber() {
        return String.valueOf(random.nextInt(1000, 99999));
    }
}
