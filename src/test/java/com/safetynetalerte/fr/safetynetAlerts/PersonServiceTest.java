package com.safetynetalerte.fr.safetynetAlerts;

import com.safetynetalerte.fr.safetynetAlerts.dto.ChildAlertDto;
import com.safetynetalerte.fr.safetynetAlerts.dto.ChildDto;
import com.safetynetalerte.fr.safetynetAlerts.exception.AddressNotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.exception.MedicalRecordNotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.model.MedicalRecord;
import com.safetynetalerte.fr.safetynetAlerts.model.Person;
import com.safetynetalerte.fr.safetynetAlerts.repository.DataRepository;
import com.safetynetalerte.fr.safetynetAlerts.service.PersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.safetynetalerte.fr.safetynetAlerts.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private DataRepository dataRepository;

    @InjectMocks
    private PersonService service;

    @Test
    @DisplayName("Adresse avec enfant et adultes : les deux listes sont remplies")
    void getChildrenByAddress_enfantsEtAdultes_retourneLesDeuxListes() {
        Person kendrik = kendrik(), brian = brian(), shawna = shawna();
        List<Person> people = List.of(kendrik, brian, shawna);
        List<MedicalRecord> records = List.of(
                medicalRecordOf(kendrik, 12),
                medicalRecordOf(brian, 40),
                medicalRecordOf(shawna, 40));
        when(dataRepository.findPeopleByAddress(ROSE)).thenReturn(people);
        when(dataRepository.findMedicalRecordsByFamily(people)).thenReturn(records);

        ChildAlertDto result = service.getChildrenByaddress(ROSE);

        assertThat(result.children()).hasSize(1);
        assertThat(result.children().get(0).getFirstName()).isEqualTo("Kendrik");
        assertThat(result.children().get(0).getAge()).isEqualTo(12);
        assertThat(result.familyMembers()).containsExactly(brianDto(), shawnaDto());
    }

    @Test
    @DisplayName("Adresse avec uniquement des adultes : deux listes vides")
    void getChildrenByAddress_seulementAdultes_retourneDeuxListesVides() {
        Person brian = brian(), shawna = shawna();
        List<Person> people = List.of(brian, shawna);
        List<MedicalRecord> records = List.of(
                medicalRecordOf(brian, 40),
                medicalRecordOf(shawna, 40));
        when(dataRepository.findPeopleByAddress(ROSE)).thenReturn(people);
        when(dataRepository.findMedicalRecordsByFamily(people)).thenReturn(records);

        ChildAlertDto result = service.getChildrenByaddress(ROSE);

        assertThat(result.children()).isEmpty();
        assertThat(result.familyMembers()).isEmpty();
    }

    @Test
    @DisplayName("Plusieurs enfants : tous sont retournés avec leur âge")
    void getChildrenByAddress_plusieursEnfants_retourneTousLesEnfants() {
        Person kendrik = kendrik(), zach = zach(), brian = brian();
        List<Person> people = List.of(kendrik, zach, brian);
        List<MedicalRecord> records = List.of(
                medicalRecordOf(kendrik, 12),
                medicalRecordOf(zach, 6),
                medicalRecordOf(brian, 40));
        when(dataRepository.findPeopleByAddress(ROSE)).thenReturn(people);
        when(dataRepository.findMedicalRecordsByFamily(people)).thenReturn(records);

        ChildAlertDto result = service.getChildrenByaddress(ROSE);

        assertThat(result.children()).extracting(ChildDto::getAge).containsExactly(12, 6);
        assertThat(result.familyMembers()).containsExactly(brianDto());
    }

    @Test
    @DisplayName("Enfant seul à l'adresse : un enfant et liste d'adultes vide")
    void getChildrenByAddress_enfantSeul_retourneFamilyMembersVide() {
        Person kendrik = kendrik();
        List<Person> people = List.of(kendrik);
        when(dataRepository.findPeopleByAddress(ROSE)).thenReturn(people);
        when(dataRepository.findMedicalRecordsByFamily(people))
                .thenReturn(List.of(medicalRecordOf(kendrik, 12)));

        ChildAlertDto result = service.getChildrenByaddress(ROSE);

        assertThat(result.children()).hasSize(1);
        assertThat(result.familyMembers()).isEmpty();
    }

    @Test
    @DisplayName("Personne sans dossier médical : MedicalRecordNotFoundException")
    void getChildrenByAddress_dossierMedicalManquant_leveMedicalRecordNotFoundException() {
        Person kendrik = kendrik(), brian = brian(), shawna = shawna();
        List<Person> people = List.of(kendrik, brian, shawna);
        // pas de dossier pour Shawna
        List<MedicalRecord> records = List.of(
                medicalRecordOf(kendrik, 12),
                medicalRecordOf(brian, 40));
        when(dataRepository.findPeopleByAddress(ROSE)).thenReturn(people);
        when(dataRepository.findMedicalRecordsByFamily(people)).thenReturn(records);

        assertThrows(MedicalRecordNotFoundException.class,
                () -> service.getChildrenByaddress(ROSE));
    }

    @Test
    @DisplayName("Adresse inconnue : l'exception du repository est propagée")
    void getChildrenByAddress_adresseInconnue_propageAddressNotFoundException() {
        when(dataRepository.findPeopleByAddress("Unknown St"))
                .thenThrow(new AddressNotFoundException("Unknown St"));

        assertThrows(AddressNotFoundException.class,
                () -> service.getChildrenByaddress("Unknown St"));

        verify(dataRepository, never()).findMedicalRecordsByFamily(anyList());
    }
}
