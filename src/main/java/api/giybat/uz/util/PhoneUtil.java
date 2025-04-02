package api.giybat.uz.util;

import java.util.regex.Pattern;

public class PhoneUtil {
    public static boolean isValidPhoneNumber(String phoneNumber) {
        String patternRegex = "^(\\+998\\d{9})$";
        return Pattern.matches(patternRegex, phoneNumber);
    }
}
