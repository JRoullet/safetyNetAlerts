package com.safetynetalerte.fr.safetynetAlerts.service;

import com.safetynetalerte.fr.safetynetAlerts.dto.FireStationCoverageDto;
import com.safetynetalerte.fr.safetynetAlerts.dto.PersonDto;
import com.safetynetalerte.fr.safetynetAlerts.dto.PhoneAlertDto;
import com.safetynetalerte.fr.safetynetAlerts.exception.FirestationNotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.model.MedicalRecord;
import com.safetynetalerte.fr.safetynetAlerts.model.Person;
import com.safetynetalerte.fr.safetynetAlerts.repository.DataRepository;
import com.safetynetalerte.fr.safetynetAlerts.utils.AgeCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FireStationService {

    private final DataRepository dataRepository;

    public FireStationService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }


    public FireStationCoverageDto getPeopleCoveredByStation(int stationNumber)  {

        List<Person> coveredPeople = findPeopleByStation(stationNumber);

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

    private List<Person> findPeopleByStation(int stationNumber) {
        // récupération des adresses liées aux firestations
        Set<String> addresses = getAdressesByStation(stationNumber);

        // récupération des personnes par addresse
        List<Person> coveredPeople = dataRepository.findPeopleByAddresses(addresses);
        log.debug("FireStation {} : {} found people", stationNumber, coveredPeople.size());
        return coveredPeople;
    }

    private Set<String> getAdressesByStation(int stationNumber) {
        Set<String> addresses = dataRepository.findAllFirestations()
                .stream()
                .filter(fs -> fs.getStation() == stationNumber)
                .map(fs -> fs.getAddress())
                .collect(Collectors.toSet());
        if (addresses.isEmpty()){
            throw new FirestationNotFoundException(stationNumber);
        }
        log.debug("Firestation {} : {} covered adresses", stationNumber, addresses.size());
        return addresses;
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

    public PhoneAlertDto getPhoneNumbersCoveredByStation(int stationNumber){
        List<Person> coveredPeople = findPeopleByStation(stationNumber);
        List<String> phoneNumbers = coveredPeople
                .stream()
                .map(Person::getPhone)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        log.debug("FireStation {} : {} distinct phone numbers", stationNumber, phoneNumbers.size());
        return new PhoneAlertDto(phoneNumbers);
    }
}
