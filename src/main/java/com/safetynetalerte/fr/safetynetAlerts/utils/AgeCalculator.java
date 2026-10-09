package com.safetynetalerte.fr.safetynetAlerts.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public final class AgeCalculator {

    // expected format of birthdate in the JSON file (MM/dd/yyyy)
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private AgeCalculator() {}

    // referenceDate is provided by the caller (the service, or a test), so this class
    // never needs to know what day it is
    public static int ageCalculation(String birthdate, LocalDate referenceDate) {
        // transforms birthdate from JSON into LocalDate, calculate and returns years
        return Period.between(LocalDate.parse(birthdate, DATE_FORMAT), referenceDate).getYears();
    }

    // according to specs : a child is 18 or younger, so an adult is strictly older than 18
    public static boolean isAdult(int age) {
        return age > 18;
    }
}
