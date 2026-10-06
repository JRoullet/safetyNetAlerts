package com.safetynetalerte.fr.safetynetAlerts.model;

import lombok.Data;

import java.util.List;

@Data
public class MedicalRecord {


    String firstName;
    String lastName;
    String birthdate;
    List<String> medications;
    List<String> allergies;


}
