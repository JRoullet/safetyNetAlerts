package com.safetynetalerte.fr.safetynetAlerts;

import com.safetynetalerte.fr.safetynetAlerts.controller.FireStationController;
import com.safetynetalerte.fr.safetynetAlerts.dto.FireStationCoverageDto;
import com.safetynetalerte.fr.safetynetAlerts.dto.PersonDto;
import com.safetynetalerte.fr.safetynetAlerts.dto.PhoneAlertDto;
import com.safetynetalerte.fr.safetynetAlerts.exception.FirestationNotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.exception.MedicalRecordNotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.service.FireStationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FireStationController.class)
public class FireStationControllerTest {

    public static final String CULVER = "1509 Culver St";
    public static final String PHONE = "841-874-6512";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FireStationService service;

    PersonDto john = new PersonDto("John","Boyd", CULVER, PHONE);
    PersonDto tenley = new PersonDto("Tenley","Boyd", CULVER, PHONE);
    List<PersonDto> people = List.of(john,tenley);

    @Test
    @DisplayName("GET /firestation?stationNumber=3 : 200 et JSON attendu")
    void getPeopleByFireStation_casPassant_retourne200() throws Exception {
        FireStationCoverageDto dto = new FireStationCoverageDto(people, 1, 1);
        when(service.getPeopleCoveredByStation(3))
                .thenReturn(dto);

        mockMvc.perform(get("/firestation").param("stationNumber", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.people.length()").value(2))
                .andExpect(jsonPath("$.people[0].firstName").value("John"))
                .andExpect(jsonPath("$.people[0].lastName").value("Boyd"))
                .andExpect(jsonPath("$.people[0].phone").value(PHONE))
                .andExpect(jsonPath("$.adultCount").value(1))
                .andExpect(jsonPath("$.childCount").value(1));

        verify(service).getPeopleCoveredByStation(3);
    }

    @Test
    @DisplayName("Station inconnue : 404")
    void getPeopleByFireStation_station_Inconnue_retourne404() throws Exception {
        var ex = new FirestationNotFoundException(999);

        when(service.getPeopleCoveredByStation(999))
                .thenThrow(ex);

        mockMvc.perform(get("/firestation").param("stationNumber", "999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ex.getMessage()));

        verify(service).getPeopleCoveredByStation(999);

    }

    @Test
    @DisplayName("Dossier médical manquant: 404")
    void getPeopleByFireStation_missingMedicalRecord_retourne404() throws Exception {
        var ex = new MedicalRecordNotFoundException("John","Boyd");

        when(service.getPeopleCoveredByStation(3))
                .thenThrow(ex);

        mockMvc.perform(get("/firestation").param("stationNumber", "3"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ex.getMessage()));

        verify(service).getPeopleCoveredByStation(3);

    }

    @Test
    @DisplayName("Paramètre stationNumber absent: 400, service non appelé")
    void getPeopleByFireStation_parametreManquant_retourne400() throws Exception {
        mockMvc.perform(get("/firestation"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);

    }

    @Test
    @DisplayName("Paramètre stationNumber non numérique: 400, service non appelé")
    void getPeopleByFireStation_parametreNonNumerique_retourne400() throws Exception {
        mockMvc.perform(get("/firestation").param("stationNumber","a"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);

    }

    @Test
    @DisplayName("GET /phoneAlert?firestation=3 : 200 et numéros attendus")
    void getPhoneNumbersCoveredByStation_casPassant_retourne200() throws Exception {
        PhoneAlertDto dto = new PhoneAlertDto(List.of("841-874-6512", "841-874-6513"));
        when(service.getPhoneNumbersCoveredByStation(3)).thenReturn(dto);

        mockMvc.perform(get("/phoneAlert").param("firestation", "3"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.phoneNumbers.length()").value(2))
                .andExpect(jsonPath("$.phoneNumbers[0]").value("841-874-6512"));

        verify(service).getPhoneNumbersCoveredByStation(3);
    }


}
