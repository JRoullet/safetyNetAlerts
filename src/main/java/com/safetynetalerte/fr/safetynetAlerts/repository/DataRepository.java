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

    // The JSON file is read only once, when Spring creates the repository at startup
    public DataRepository(ObjectMapper mapper) throws IOException {

        // getResourceAsStream looks for the file in the classpath (src/main/resources is copied
        // to target/classes), so it works from the IDE, from Maven and from a packaged JAR.
        // The leading "/" means "start from the root of the classpath".
        // It returns null if the file is not found.
        // try-with-resources closes the stream automatically, even if an exception is thrown
        try (InputStream inputStream = getClass().getResourceAsStream("/data-safetynet.json")) {

            // Jackson reads the stream and builds a SafetyNetData object
            // the attribute names of the class must match the keys of the JSON
            // (persons, firestations, medicalrecords)
            this.data = mapper.readValue(inputStream, SafetyNetData.class);
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
