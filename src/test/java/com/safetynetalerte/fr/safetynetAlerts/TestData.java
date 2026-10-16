package com.safetynetalerte.fr.safetynetAlerts;

import com.safetynetalerte.fr.safetynetAlerts.model.Firestation;
import com.safetynetalerte.fr.safetynetAlerts.model.MedicalRecord;
import com.safetynetalerte.fr.safetynetAlerts.model.Person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class TestData {

    public static final String CULVER = "1509 Culver St";
    public static final String CITY = "Culver";
    public static final String ZIP = "97451";
    // format de birthdate du Json à transmettre
    private static final DateTimeFormatter BIRTHDATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static final List<String> ALLERGIES = List.of("peanut", "shellfish", "aznol");
    public static final List<String> MEDICATIONS = List.of("aznol:350mg", "hydrapermazol:100mg");

    private TestData() {
    }

    public static Person john()   { return person("John", "Boyd", CULVER, CITY, ZIP, "841-874-6512","jaboyd@email.com"); }
    public static Person jacob()  { return person("Jacob", "Boyd", CULVER, CITY, ZIP, "841-874-6513","drk@email.com"); }
    public static Person tenley() { return person("Tenley", "Boyd", CULVER, CITY, ZIP, "841-874-6512","tenz@email.com"); }

    public static Person person(String firstName, String lastName, String address, String city, String zip, String phone, String email) {
        Person p = new Person();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setAddress(address);
        p.setCity(city);
        p.setZip(zip);
        p.setPhone(phone);
        p.setEmail(email);
        return p;
    }

    public static Firestation firestation(String address, int station) {
        Firestation fs = new Firestation();
        fs.setAddress(address);
        fs.setStation(station);
        return fs;
    }

    public static MedicalRecord medicalRecordOf(Person person, int ageInYears) {
        MedicalRecord mr = new MedicalRecord();
        mr.setFirstName(person.getFirstName());
        mr.setLastName(person.getLastName());
        mr.setBirthdate(LocalDate.now().minusYears(ageInYears).format(BIRTHDATE_FORMAT));
        mr.setMedications(MEDICATIONS);
        mr.setAllergies(ALLERGIES);
        return mr;
    }
}