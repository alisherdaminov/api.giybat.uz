package api.giybat.uz.util;

import java.util.regex.Pattern;

public class EmailUtil {
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        return Pattern.matches(emailRegex, email);
    }
}
