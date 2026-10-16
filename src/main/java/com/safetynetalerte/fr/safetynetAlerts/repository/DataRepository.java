package com.safetynetalerte.fr.safetynetAlerts.repository;

import com.safetynetalerte.fr.safetynetAlerts.exception.MedicalRecordNotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.model.Firestation;
import com.safetynetalerte.fr.safetynetAlerts.model.MedicalRecord;
import com.safetynetalerte.fr.safetynetAlerts.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

@Repository
public class DataRepository {

    private final SafetyNetData data;

    // Par défaut pour Spring au démarrage
    @Autowired
    public DataRepository(ObjectMapper mapper) throws IOException {
        this(mapper, "/json/data-safetynet.json");
    }

    /**
     * Ajout du resourcePath en paramètre du constructeur pour avoir le choix du fichier d'entrée
     * => permet d'implémenter les cas de test
     * @param mapper
     * @param resourcePath
     * @throws IOException
     */
    // For tests
    public DataRepository(ObjectMapper mapper, String resourcePath) throws IOException {
        // getResourceAsStream cherche le fichier dans le chemin (src/main/resources est copié vers la cible/classe),
        // fonctionne depuis l'IDE, Maven ou un package JAR
        try (InputStream inputStream = getClass().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Fichier de données introuvable : " + resourcePath);
            }
            // Jackson lit le stream et construit l'objet SafetyNetData
            // les attributs doivent correspondre aux éléments clés déclarés dans le JSON
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

    public List<Person> findPeopleByAddresses(Set<String> addresses) {
        return data.getPeople()
                .stream()
                .filter(p -> addresses.contains(p.getAddress()))
                .toList();
    }

    public MedicalRecord findMedicalRecordByName(String firstName, String lastName) {
    return data.getMedicalRecords()
            .stream()
            .filter(medicalRecord -> firstName.equals(medicalRecord.getFirstName()) && lastName.equals(medicalRecord.getLastName()))
            .findFirst().orElseThrow(() -> new MedicalRecordNotFoundException(firstName, lastName));
    }

}
