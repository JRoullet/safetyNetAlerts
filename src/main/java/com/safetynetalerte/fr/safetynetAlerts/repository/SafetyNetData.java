package com.safetynetalerte.fr.safetynetAlerts.repository;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.safetynetalerte.fr.safetynetAlerts.model.Firestation;
import com.safetynetalerte.fr.safetynetAlerts.model.MedicalRecord;
import com.safetynetalerte.fr.safetynetAlerts.model.Person;
import lombok.Data;

import java.util.List;

@Data
public class SafetyNetData {

    @JsonProperty("persons")
    private List<Person> people;

    private List<Firestation> firestations;

    @JsonProperty("medicalrecords")
    private List<MedicalRecord> medicalRecords;

}
