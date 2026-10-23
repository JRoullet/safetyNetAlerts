package com.safetynetalerte.fr.safetynetAlerts;

import com.safetynetalerte.fr.safetynetAlerts.exception.AddressNotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.exception.MedicalRecordNotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.model.Person;
import com.safetynetalerte.fr.safetynetAlerts.repository.DataRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class DataRepositoryTests {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Testing du fichier Json d'entree - utilisation de la méthode loadFrom pour charger les différents fichiers
     * @param resource (fichier json)
     * @return DataRepository
     * @throws IOException
     */
    private DataRepository loadFrom(String resource) throws IOException {
        return new DataRepository(mapper, "/json/" + resource);
    }


    /** Cas passants
     *
     * @throws IOException
     */
    @Test
    @DisplayName("Un fichier valide charge personnes, casernes et dossiers médicaux")
    void validFile_loadsAllSections() throws IOException {
        DataRepository repository = loadFrom("valid.json");

        assertEquals(2, repository.findAllPeople().size());
        assertEquals(1, repository.findAllFirestations().size());
        assertEquals(1, repository.findAllMedicalRecords().size());
    }

    @Test
    @DisplayName("Un fichier valide mappe correctement les champs")
    void validFile_mapsFields() throws IOException {
        DataRepository repository = loadFrom("valid.json");

        Person first = repository.findAllPeople().get(0);
        assertEquals("John", first.getFirstName());
        assertEquals("Boyd", first.getLastName());
        assertEquals("1509 Culver St", first.getAddress());
        assertEquals(3, repository.findAllFirestations().get(0).getStation());
    }

    /** Inexistence du fichier renvoie une IllegalState Exception
     *
     */
    @Test
    @DisplayName("Un fichier inexistant lève une IllegalStateException avec le chemin")
    void missingFile_throwsException() {
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> loadFrom("does-not-exist.json"));
        assertTrue(e.getMessage().contains("does-not-exist.json"));
    }

    /**
     * Levée d'exceptions Jackson liées à l'impossibilité de lire le fichier d'entrée
     */
    @Test
    @DisplayName("Un fichier vide lève une exception de lecture")
    void emptyFile_throwsException() {
        assertThrows(JacksonException.class, () -> loadFrom("empty.json"));
    }

    @Test
    @DisplayName("Un JSON mal formé lève une exception de lecture")
    void malformedFile_throwsException() {
        assertThrows(JacksonException.class, () -> loadFrom("malformed.json"));
    }

    @Test
    @DisplayName("Un numéro de station non numérique lève une exception")
    void wrongTypeFile_throwsException() {
        assertThrows(JacksonException.class, () -> loadFrom("wrong-type.json"));
    }

    @Test
    @DisplayName("Une section absente renvoie une liste vide, pas null")
    void missingSection_returnsEmptyList() throws IOException {
        DataRepository repository = loadFrom("missing-section.json");

        assertNotNull(repository.findAllFirestations());
        assertTrue(repository.findAllFirestations().isEmpty());
    }

    @Test
    @DisplayName("Un dossier médical inconnu lève MedicalRecordNotFoundException")
    void findMedicalRecordByName_unknown_throws() throws IOException {
        DataRepository repository = loadFrom("valid.json");

        MedicalRecordNotFoundException e = assertThrows(MedicalRecordNotFoundException.class,
                () -> repository.findMedicalRecordByName("Soldat", "Inconnu"));
        assertTrue(e.getMessage().contains("Soldat Inconnu"));
    }

    @Test
    @DisplayName("Une addresse inconnue lève AddressNotFoundException")
    void findPeopleByAddress_adresseInconnue_leveAddressNotFoundException() throws IOException {
        DataRepository repository = loadFrom("valid.json");
        assertThrows(AddressNotFoundException.class,
                () -> repository.findPeopleByAddress("Unknown St"));
    }

    /**
     *  Tests d'intégration sur le vrai fichier de données
     * @throws IOException
     */

    @Test
    @DisplayName("Le vrai fichier contient 23 personnes")
    void realFile_contains23People() throws IOException {
        DataRepository repository = new DataRepository(mapper);
        assertEquals(23, repository.findAllPeople().size());
    }

    @Test
    @DisplayName("Le vrai fichier contient 13 casernes")
    void realFile_contains13Firestations() throws IOException {
        DataRepository repository = new DataRepository(mapper);
        assertEquals(13, repository.findAllFirestations().size());
    }

    @Test
    @DisplayName("Le vrai fichier contient 23 dossiers médicaux")
    void realFile_contains23MedicalRecords() throws IOException {
        DataRepository repository = new DataRepository(mapper);
        assertEquals(23, repository.findAllMedicalRecords().size());
    }


}
