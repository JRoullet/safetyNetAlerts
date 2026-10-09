package com.safetynetalerte.fr.safetynetAlerts.service;

import com.safetynetalerte.fr.safetynetAlerts.dto.FireStationCoverageDto;
import com.safetynetalerte.fr.safetynetAlerts.dto.PersonDto;
import com.safetynetalerte.fr.safetynetAlerts.model.MedicalRecord;
import com.safetynetalerte.fr.safetynetAlerts.model.Person;
import com.safetynetalerte.fr.safetynetAlerts.repository.DataRepository;
import com.safetynetalerte.fr.safetynetAlerts.utils.AgeCalculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FireStationService {

    private final DataRepository dataRepository;

    public FireStationService(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public FireStationCoverageDto getPeopleCoveredByStation(int stationNumber) {
        // retrieve addresses related to each firestation
        Set<String> addresses = dataRepository.findAllFirestations()
                .stream()
                .filter(fs -> fs.getStation() == stationNumber)
                .map(Firestation -> Firestation.getAddress())
                .collect(Collectors.toSet());
        log.debug("Firestation {} : {} covered adresses", stationNumber, addresses.size());

        // retrieve covered people by address
        List<Person> coveredPeople = dataRepository.findPeopleByAddresses(addresses);
        log.debug("FireStation {} : {} found people", stationNumber, coveredPeople.size());

        // filling in personDto fields to match expected return
        List<PersonDto> people = coveredPeople
                .stream()
                .map(p -> new PersonDto(p.getFirstName(), p.getLastName(), p.getAddress(), p.getPhone()))
                .toList();

        // initialize adults & children count
        long adults = 0;
        long children = 0;
        // iteration on each person to count adults and children
        for (Person p : coveredPeople) {
            // retrieve individual medicalRecords
            Optional<MedicalRecord> medicalRecord =
                    dataRepository.findMedicalRecordByName(p.getFirstName(), p.getLastName());

            // age management
            LocalDate today = LocalDate.now();
            String birthdate = medicalRecord.get().getBirthdate();
            int age = AgeCalculator.ageCalculation(birthdate, today);
            boolean adult = AgeCalculator.isAdult(age);
            log.debug("FireStation {} : related file of {} {}, {} years old, {}",
                    stationNumber, p.getFirstName(), p.getLastName(), age, adult ? "adulte" : "enfant");
            if (adult) {
                adults++;
            } else {
                children++;
            }
        }
        return new FireStationCoverageDto(people,adults,children);
    }
}
