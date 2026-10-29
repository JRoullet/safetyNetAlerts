package com.safetynetalerte.fr.safetynetAlerts.service;

import com.safetynetalerte.fr.safetynetAlerts.dto.ChildAlertDto;
import com.safetynetalerte.fr.safetynetAlerts.dto.ChildDto;
import com.safetynetalerte.fr.safetynetAlerts.dto.FireAlertDto;
import com.safetynetalerte.fr.safetynetAlerts.dto.PersonNameDto;
import com.safetynetalerte.fr.safetynetAlerts.exception.MedicalRecordNotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.model.MedicalRecord;
import com.safetynetalerte.fr.safetynetAlerts.model.Person;
import com.safetynetalerte.fr.safetynetAlerts.repository.DataRepository;
import com.safetynetalerte.fr.safetynetAlerts.utils.AgeCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PersonService {


    private final DataRepository dataRepository;

    public PersonService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public ChildAlertDto getChildrenByaddress(String address){

        LocalDate referenceDate = LocalDate.now();

        //Recupérer la liste des personnes par adresse
        List<Person> familyList = dataRepository.findPeopleByAddress(address);
        log.info("Récupération de {} personnes pour l'adresse {}",
                familyList.size(), address);

        //Recupérer la liste des medical records pour la famille afin de récupérer l'âge de chaque personne
        List<MedicalRecord> medicalRecords = dataRepository.findMedicalRecordsByFamily(familyList);
        log.info("Récupération de {} dossiers médicaux pour la famille",
                medicalRecords.size());

        ChildAlertDto result = extractFamilyMembers(
                familyList, medicalRecords, referenceDate);

        log.info("Résultat de child alert : {} enfants, {} adultes",
                result.children() == null ? 0 : result.children().size(),
                result.familyMembers().size());

        return result;
    }


    private ChildAlertDto extractFamilyMembers(
            List<Person> familyList,
            List<MedicalRecord> medicalRecords,
            LocalDate referenceDate) {

        List<ChildDto> children = new ArrayList<>();
        List<PersonNameDto> familyMembers = new ArrayList<>();

        // Iteration sur les medical records pour aller chercher la date de naissance d'une personne
        for (Person person : familyList) {
            MedicalRecord record = medicalRecords.stream()
                    .filter(m -> m.getFirstName().equals(person.getFirstName())
                            && m.getLastName().equals(person.getLastName()))
                    .findFirst()
                    .orElseThrow(() -> new MedicalRecordNotFoundException(person.getFirstName(), person.getLastName()));

            if (record == null) {
                log.warn("Aucun dossier médical trouvé pour {} {}",
                        person.getFirstName(), person.getLastName());
                continue;
            }

            int age = AgeCalculator.ageCalculation(
                    record.getBirthdate(), referenceDate);

            if (AgeCalculator.isAdult(age)) {
                PersonNameDto adult = new PersonNameDto(
                        person.getFirstName(),
                        person.getLastName()
                );
                familyMembers.add(adult);
            } else {
                ChildDto child = new ChildDto(
                        person.getFirstName(),
                        person.getLastName(),
                        age
                );
                children.add(child);
            }
        }

        //Une fois qu'on a bouclé sur tous les enfants, s'il n'y en a aucun, on renvoie 2 listes vides
        if (children.isEmpty()) {
            return new ChildAlertDto(new ArrayList<>(), new ArrayList<>());
        }
        else {
            return new ChildAlertDto(children, familyMembers);
        }
    }


    public FireAlertDto getFireAlertByAddress(String address) {
        return null;
    }
}
