package com.safetynetalerte.fr.safetynetAlerts.repository;

import com.safetynetalerte.fr.safetynetAlerts.exception.AddressNotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.exception.FirestationNotFoundException;
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

    // Type d'entrée différent spécifique à la requête, plus simple d'aller dans ce sens plutôt que de complexifier en aval les tests p
    // our s'adapter alors qu'il s'agit juste d'une méthode simple
    public List<Person> findPeopleByAddress(String address) {
        List<Person> people = data.getPeople()
                .stream()
                .filter(p -> p.getAddress().equals(address))
                .toList();
        if (people.isEmpty()) {
            throw new AddressNotFoundException(address);
        }
        return people;
    }

    public MedicalRecord findMedicalRecordByName(String firstName, String lastName) {
    return data.getMedicalRecords()
            .stream()
            .filter(medicalRecord -> firstName.equals(medicalRecord.getFirstName()) && lastName.equals(medicalRecord.getLastName()))
            .findFirst().orElseThrow(() -> new MedicalRecordNotFoundException(firstName, lastName));
    }

    public List<MedicalRecord> findMedicalRecordsByFamily(List<Person> people) {
    return data.getMedicalRecords()
            .stream()
            .filter(medicalRecord -> people.stream()
                    .anyMatch(person -> person.getFirstName().equals(medicalRecord.getFirstName())
                            && person.getLastName().equals(medicalRecord.getLastName())))
            .toList();
    }

    public Firestation findFirestationByAddress(String address) {
        return data.getFirestations()
                .stream()
                .filter(fs -> address.equals(fs.getAddress()))
                .findFirst()
                .orElseThrow(() -> new FirestationNotFoundException(address));
    }
}
