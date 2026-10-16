package com.safetynetalerte.fr.safetynetAlerts.service;

import com.safetynetalerte.fr.safetynetAlerts.dto.FireStationCoverageDto;
import com.safetynetalerte.fr.safetynetAlerts.dto.PersonDto;
import com.safetynetalerte.fr.safetynetAlerts.exception.FirestationNotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.model.MedicalRecord;
import com.safetynetalerte.fr.safetynetAlerts.model.Person;
import com.safetynetalerte.fr.safetynetAlerts.repository.DataRepository;
import com.safetynetalerte.fr.safetynetAlerts.utils.AgeCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FireStationService {

    private final DataRepository dataRepository;

    public FireStationService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    //TODO
    // Tester avec postman
    // TU à implémenter (cas classiques pour la couverture de code)
    // Tests sur les enpoints (mockmvc)
    public FireStationCoverageDto getPeopleCoveredByStation(int stationNumber)  {

        // récupération des adresses liées aux firestations
        Set<String> addresses = dataRepository.findAllFirestations()
                .stream()
                .filter(fs -> fs.getStation() == stationNumber)
                .map(fs -> fs.getAddress())
                .collect(Collectors.toSet());
        if (addresses.isEmpty()){
            throw new FirestationNotFoundException(stationNumber);
        }
        log.debug("Firestation {} : {} covered adresses", stationNumber, addresses.size());

        // récupération des personnes par addresse
        List<Person> coveredPeople = dataRepository.findPeopleByAddresses(addresses);
        log.debug("FireStation {} : {} found people", stationNumber, coveredPeople.size());

        // remplissage du dto pour matcher le format de retour attendu
        List<PersonDto> people = coveredPeople
                .stream()
                .map(p -> new PersonDto(p.getFirstName(), p.getLastName(), p.getAddress(), p.getPhone()))
                .toList();

        // init du compte des adultes et enfants
        long adults = 0;
        long children = 0;
        // itération sur chaque personne pour compter adultes et enfants
        for (Person person : coveredPeople) {
            // récupération des medicalRecords par personne
            MedicalRecord medicalRecord = dataRepository.findMedicalRecordByName(person.getFirstName(), person.getLastName());
            boolean adult = isAdult(stationNumber, person, medicalRecord);
            if (adult) {
                adults++;
            } else {
                children++;
            }
        }
        return new FireStationCoverageDto(people,adults,children);
    }

    private static boolean isAdult(int stationNumber, Person p, MedicalRecord medicalRecord) {
        // gestion de l'âge
        LocalDate today = LocalDate.now();
        String birthdate = medicalRecord.getBirthdate();
        int age = AgeCalculator.ageCalculation(birthdate, today);
        boolean adult = AgeCalculator.isAdult(age);
        log.debug("FireStation {} : related file of {} {}, {} years old, {}",
                stationNumber, p.getFirstName(), p.getLastName(), age, adult ? "adulte" : "enfant");
        return adult;
    }
}
