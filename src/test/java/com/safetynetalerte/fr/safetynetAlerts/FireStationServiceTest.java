package com.safetynetalerte.fr.safetynetAlerts;

import com.safetynetalerte.fr.safetynetAlerts.dto.FireStationCoverageDto;
import com.safetynetalerte.fr.safetynetAlerts.exception.FirestationNotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.exception.MedicalRecordNotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.model.Person;
import com.safetynetalerte.fr.safetynetAlerts.repository.DataRepository;
import com.safetynetalerte.fr.safetynetAlerts.service.FireStationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static com.safetynetalerte.fr.safetynetAlerts.TestData.firestation;
import static com.safetynetalerte.fr.safetynetAlerts.TestData.medicalRecordOf;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FireStationServiceTest {


    /**
     * Addresses
     */
    public static final String CULVER = "1509 Culver St";
    public static final String BINOC = "834 Binoc Ave";
    public static final String STEPPES = "112 Steppes Pl";

    /**
     * Pour le test de FireStationService, on mock le retour du repository, car on teste uniquement le service
     */
    @Mock
    private DataRepository dataRepository;

    /**
     * On injecte les mocks dans le service à tester
     */
    @InjectMocks
    private FireStationService service;

    /**
     * Préparation des bouchons pour limiter la recréation d'objets dans chaque test / classe de test
     */
    private final Person john = TestData.john();
    private final Person jacob = TestData.jacob();
    private final Person tenley = TestData.tenley();

    // passant
    @Test
    @DisplayName("Station connue : renvoie les personnes, le bon nombre d'adultes et d'enfants")
    void getPeopleCoveredByStation_returnsPeopleAndCounts() {

        //given
        when(dataRepository.findAllFirestations())
                .thenReturn(List.of(firestation(CULVER, 3)));
        when(dataRepository.findPeopleByAddresses(Set.of(CULVER)))
                .thenReturn(List.of(john, jacob, tenley));
        when(dataRepository.findMedicalRecordByName("John", "Boyd"))
                .thenReturn(medicalRecordOf(john, 40));
        when(dataRepository.findMedicalRecordByName("Jacob", "Boyd"))
                .thenReturn(medicalRecordOf(jacob, 35));
        when(dataRepository.findMedicalRecordByName("Tenley", "Boyd"))
                .thenReturn(medicalRecordOf(tenley, 5));

        //when
        FireStationCoverageDto result = service.getPeopleCoveredByStation(3);

        //assert
        assertEquals(3, result.people().size());
        assertEquals(2, result.adultCount());
        assertEquals(1, result.childCount());
    }

    // retour adresses liées à la station seulement
    @Test
    @DisplayName("Station connue : seules les adresses de cette station sont recherchées")
    void getPeopleCoveredByStation_usesOnlyAddressesOfRequestedStation() {
        //given
        when(dataRepository.findAllFirestations()).thenReturn(List.of(
                firestation(CULVER, 3),
                firestation(BINOC, 3),
                firestation(STEPPES, 4)));
        when(dataRepository.findPeopleByAddresses(any())).thenReturn(List.of());

        //when
        service.getPeopleCoveredByStation(3);

        //assert
        verify(dataRepository).findPeopleByAddresses(Set.of(CULVER, BINOC));
    }

    // erreur
    @Test
    @DisplayName("Station inconnue : lève FirestationNotFoundException")
    void getPeopleCoveredByStation_unknownStation_throws() {
        when(dataRepository.findAllFirestations())
                .thenReturn(List.of(firestation(CULVER, 3)));

        FirestationNotFoundException e = assertThrows(FirestationNotFoundException.class,
                () -> service.getPeopleCoveredByStation(999));

        assertTrue(e.getMessage().contains("999"));
    }

    // retourne liste de personnes vide
    @Test
    @DisplayName("Station sans habitant : renvoie une liste vide et 0 adulte, 0 enfant")
    void getPeopleCoveredByStation_noPeopleAtAddresses_returnsEmptyResult() {
        when(dataRepository.findAllFirestations())
                .thenReturn(List.of(firestation(CULVER, 3)));
        when(dataRepository.findPeopleByAddresses(Set.of(CULVER)))
                .thenReturn(List.of());

        FireStationCoverageDto result = service.getPeopleCoveredByStation(3);

        assertTrue(result.people().isEmpty());
        assertEquals(0, result.adultCount());
        assertEquals(0, result.childCount());
    }

    // exception medical record absent lève l'exception
    @Test
    @DisplayName("Personne sans dossier médical : l'exception remonte")
    void getPeopleCoveredByStation_missingMedicalRecord_throws() {
        when(dataRepository.findAllFirestations())
                .thenReturn(List.of(firestation(CULVER, 3)));
        when(dataRepository.findPeopleByAddresses(Set.of(CULVER)))
                .thenReturn(List.of(john));
        when(dataRepository.findMedicalRecordByName("John", "Boyd"))
                .thenThrow(new MedicalRecordNotFoundException("John", "Boyd"));

        assertThrows(MedicalRecordNotFoundException.class,
                () -> service.getPeopleCoveredByStation(3));
    }

}