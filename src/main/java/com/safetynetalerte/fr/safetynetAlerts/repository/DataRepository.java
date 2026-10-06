package com.safetynetalerte.fr.safetynetAlerts.repository;

import com.safetynetalerte.fr.safetynetAlerts.model.Firestation;
import com.safetynetalerte.fr.safetynetAlerts.model.MedicalRecord;
import com.safetynetalerte.fr.safetynetAlerts.model.Person;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Repository
public class DataRepository {

    private final SafetyNetData data;

    public DataRepository(ObjectMapper mapper) throws IOException {
        try (InputStream in = getClass().getResourceAsStream("/data-safetynet.json")) {
            this.data = mapper.readValue(in, SafetyNetData.class);
        }
    }

    public List<Person> findAllPeople() {
        return data.getPeople();
    }

    public List<Firestation> findAllFirestations() {
        return data.getFirestations();
    }

    public List<MedicalRecord> findAllMedicalRecords() {
        return data.getMedicalRecords();
    }



}
