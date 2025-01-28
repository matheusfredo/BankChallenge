package br.com.compass.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidationUtils {

    public static boolean isValidCpf(String cpf) {
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += (cpf.charAt(i) - '0') * (10 - i);
            }
            int firstDigit = 11 - (sum % 11);
            if (firstDigit >= 10) {
                firstDigit = 0;
            }
            if (firstDigit != (cpf.charAt(9) - '0')) {
                return false;
            }
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += (cpf.charAt(i) - '0') * (11 - i);
            }
            int secondDigit = 11 - (sum % 11);
            if (secondDigit >= 10) {
                secondDigit = 0;
            }
            return secondDigit == (cpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasRepeatedDigits(String password) {
        for (int i = 0; i < password.length(); i++) {
            for (int j = i + 1; j < password.length(); j++) {
                if (password.charAt(i) == password.charAt(j)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isValidName(String name) {
        return name.matches("^[a-zA-Z\\s]+$") && name.length() >= 2 && name.length() <= 100;
    }

    public static boolean isValidPhone(String phone) {
        return phone.length() == 11 && phone.matches("\\d+");
    }

    public static boolean isValidBirthDate(String birthDate) {
        try {
            LocalDate date = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            LocalDate today = LocalDate.now();
            return !date.isBefore(LocalDate.of(1900, 1, 1)) && date.isBefore(today.minusYears(18));
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
