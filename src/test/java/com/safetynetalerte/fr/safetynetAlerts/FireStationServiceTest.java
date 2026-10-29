package com.safetynetalerte.fr.safetynetAlerts;

import com.safetynetalerte.fr.safetynetAlerts.dto.FireStationCoverageDto;
import com.safetynetalerte.fr.safetynetAlerts.dto.PhoneAlertDto;
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

import static com.safetynetalerte.fr.safetynetAlerts.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FireStationServiceTest {

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
        //given
        when(dataRepository.findAllFirestations())
                .thenReturn(List.of(firestation(CULVER, 3)));

        //when
        FirestationNotFoundException e = assertThrows(FirestationNotFoundException.class,
                () -> service.getPeopleCoveredByStation(999));

        //assert
        assertTrue(e.getMessage().contains("999"));
    }

    // retourne liste de personnes vide
    @Test
    @DisplayName("Station sans habitant : renvoie une liste vide et 0 adulte, 0 enfant")
    void getPeopleCoveredByStation_noPeopleAtAddresses_returnsEmptyResult() {
        //given
        when(dataRepository.findAllFirestations())
                .thenReturn(List.of(firestation(CULVER, 3)));
        when(dataRepository.findPeopleByAddresses(Set.of(CULVER)))
                .thenReturn(List.of());

        //when
        FireStationCoverageDto result = service.getPeopleCoveredByStation(3);

        //assert
        assertTrue(result.people().isEmpty());
        assertEquals(0, result.adultCount());
        assertEquals(0, result.childCount());
    }

    // exception medical record absent lève l'exception
    @Test
    @DisplayName("Personne sans dossier médical : l'exception remonte")
    void getPeopleCoveredByStation_missingMedicalRecord_throws() {
        //given
        when(dataRepository.findAllFirestations())
                .thenReturn(List.of(firestation(CULVER, 3)));
        when(dataRepository.findPeopleByAddresses(Set.of(CULVER)))
                .thenReturn(List.of(john));

        //when
        when(dataRepository.findMedicalRecordByName("John", "Boyd"))
                .thenThrow(new MedicalRecordNotFoundException("John", "Boyd"));

        //assert
        assertThrows(MedicalRecordNotFoundException.class,
                () -> service.getPeopleCoveredByStation(3));
    }

    @Test
    @DisplayName("Numéros de plusieurs adresses desservies par la même station")
    void getPhoneNumbersCoveredByStation_plusieursAdresses_retourneLesNumeros() {
        // given : la station 3 dessert deux adresses, la station 2 une autre
        when(dataRepository.findAllFirestations()).thenReturn(List.of(
                firestation(CULVER, 3),
                firestation(ROSE, 3),
                firestation("112 Steppes Pl", 2)));

        // trois résidents, trois numéros différents
        when(dataRepository.findPeopleByAddresses(Set.of(CULVER, ROSE))).thenReturn(List.of(
                john(),    // 841-874-6512
                jacob(),   // 841-874-6513
                brian())); // 841-874-7784

        // when
        PhoneAlertDto result = service.getPhoneNumbersCoveredByStation(3);

        // then
        assertThat(result.phoneNumbers())
                .containsExactly("841-874-6512", "841-874-6513", "841-874-7784");
    }

    @Test
    @DisplayName("Deux personnes avec le même numéro : un seul numéro dans la liste")
    void getPhoneNumbersCoveredByStation_numerosEnDouble_retourneSansDoublons() {
        // given
        when(dataRepository.findAllFirestations()).thenReturn(List.of(firestation(CULVER, 3)));
        // john et tenley partagent 841-874-6512, jacob a le 841-874-6513
        when(dataRepository.findPeopleByAddresses(Set.of(CULVER)))
                .thenReturn(List.of(john(), jacob(), tenley()));
        // when
        PhoneAlertDto result = service.getPhoneNumbersCoveredByStation(3);

        // then
        assertThat(result.phoneNumbers()).containsExactly("841-874-6512", "841-874-6513");
    }

    @Test
    @DisplayName("Personne sans numéro de téléphone : ignorée de la liste")
    void getPhoneNumbersCoveredByStation_numeroNull_estIgnore() {
        // given
        Person sansNumero = brian();
        sansNumero.setPhone(null);
        when(dataRepository.findAllFirestations()).thenReturn(List.of(firestation(CULVER, 3)));
        when(dataRepository.findPeopleByAddresses(Set.of(CULVER)))
                .thenReturn(List.of(john(), sansNumero));
        // when
        PhoneAlertDto result = service.getPhoneNumbersCoveredByStation(3);

        // then
        assertThat(result.phoneNumbers()).containsExactly("841-874-6512");
    }

    @Test
    @DisplayName("Station inconnue : FirestationNotFoundException, aucune recherche de personnes")
    void getPhoneNumbersCoveredByStation_stationInconnue_leveFirestationNotFoundException() {
        // given
        when(dataRepository.findAllFirestations()).thenReturn(List.of(firestation(CULVER, 3)));
        // when
        FirestationNotFoundException e = assertThrows(FirestationNotFoundException.class,
                () -> service.getPhoneNumbersCoveredByStation(999));
        // then
        assertEquals("No firestation was found for id : 999", e.getMessage());
        verify(dataRepository, never()).findPeopleByAddresses(anySet());
    }

    @Test
    @DisplayName("Station sans habitant : liste vide")
    void getPhoneNumbersCoveredByStation_aucunHabitant_retourneListeVide() {
        // given
        when(dataRepository.findAllFirestations()).thenReturn(List.of(firestation(CULVER, 3)));
        when(dataRepository.findPeopleByAddresses(Set.of(CULVER))).thenReturn(List.of());

        // when
        PhoneAlertDto result = service.getPhoneNumbersCoveredByStation(3);

        // then
        assertThat(result.phoneNumbers()).isEmpty();
    }


}