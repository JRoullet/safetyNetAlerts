package com.safetynetalerte.fr.safetynetAlerts;

import com.safetynetalerte.fr.safetynetAlerts.dto.ChildDto;
import com.safetynetalerte.fr.safetynetAlerts.dto.PersonNameDto;
import com.safetynetalerte.fr.safetynetAlerts.model.Firestation;
import com.safetynetalerte.fr.safetynetAlerts.model.MedicalRecord;
import com.safetynetalerte.fr.safetynetAlerts.model.Person;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class TestData {

    // Adresses
    public static final String CULVER = "1509 Culver St";
    public static final String BINOC = "834 Binoc Ave";
    public static final String STEPPES = "112 Steppes Pl";
    public static final String ROSE = "947 E. Rose Dr";

    public static final String CITY = "Culver";
    public static final String ZIP = "97451";

    // format de birthdate du Json à transmettre
    private static final DateTimeFormatter BIRTHDATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    public static final List<String> ALLERGIES = List.of("peanut", "shellfish", "aznol");
    public static final List<String> MEDICATIONS = List.of("aznol:350mg", "hydrapermazol:100mg");

    private TestData() {
    }

    // Person
    public static Person john()   { return person("John", "Boyd", CULVER, CITY, ZIP, "841-874-6512","jaboyd@email.com"); }
    public static Person jacob()  { return person("Jacob", "Boyd", CULVER, CITY, ZIP, "841-874-6513","drk@email.com"); }
    public static Person tenley() { return person("Tenley", "Boyd", CULVER, CITY, ZIP, "841-874-6512","tenz@email.com"); }

    public static Person brian()   { return person("Brian", "Stelzer", ROSE); }
    public static Person shawna()  { return person("Shawna", "Stelzer", ROSE); }
    public static Person kendrik() { return person("Kendrik", "Stelzer", ROSE); }
    public static Person zach()    { return person("Zach", "Stelzer", ROSE); }

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

    // Constructeur surchargé : valeurs par défaut pour les champs sans importance dans le test
    public static Person person(String firstName, String lastName, String address) {
        return person(firstName, lastName, address, CITY, ZIP, "841-874-7784",
                firstName.toLowerCase() + "@email.com");
    }

    // DTO : valeurs attendues du service vers le controlleur

    public static PersonNameDto personNameDto(String firstName, String lastName) {
        return new PersonNameDto(firstName, lastName);
    }

    public static PersonNameDto brianDto()  { return personNameDto("Brian", "Stelzer"); }
    public static PersonNameDto shawnaDto() { return personNameDto("Shawna", "Stelzer"); }


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