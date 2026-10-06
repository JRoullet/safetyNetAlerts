package com.safetynetalerte.fr.safetynetAlerts;

import com.safetynetalerte.fr.safetynetAlerts.repository.DataRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

public class DataRepositoryTests {

    private DataRepository dataRepository;

    @BeforeEach
    void setUp() throws IOException {
        dataRepository = new DataRepository(new ObjectMapper());
    }

    @Test
    @DisplayName("Find All People returns 23")
    void findAllPeopleReturns23PeopleTest(){
        dataRepository.findAllPeople();
        Assertions.assertEquals(23, dataRepository.findAllPeople().size());
    }

    @Test
    @DisplayName("Find All FireStations returns 13")
    void findAllFireStationsReturns13FSTest(){
        dataRepository.findAllFirestations();
        Assertions.assertEquals(13, dataRepository.findAllFirestations().size());
    }

    @Test
    @DisplayName("Find All MedicalRecords returns 23")
    void findAllFireStationsReturns23MRTest(){
        dataRepository.findAllMedicalRecords();
        Assertions.assertEquals(23, dataRepository.findAllMedicalRecords().size());
    }
}
