package com.wadpam.open.web;

import java.util.regex.Pattern;

/**
 * Contains various validation functions.
 * @author mattiaslevin
 */
public class ValidationUtils {

    // Check if it is a valid email format
    public static boolean isValidEmailFormat(String email) {
        Pattern pattern = Pattern.compile(
                "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");
        return pattern.matcher(email).matches();
    }

    // Check if it is a valid email format, will allow capitals in the email, e.g. Mattias.Levin@gmail.com
    public static boolean isValidEmailFormatAllowCapitals(String email) {
        return ValidationUtils.isValidEmailFormat(email.toLowerCase());
    }

}
