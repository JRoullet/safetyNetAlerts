package com.safetynetalerte.fr.safetynetAlerts;

import com.safetynetalerte.fr.safetynetAlerts.controller.PersonController;
import com.safetynetalerte.fr.safetynetAlerts.dto.ChildAlertDto;
import com.safetynetalerte.fr.safetynetAlerts.dto.ChildDto;
import com.safetynetalerte.fr.safetynetAlerts.dto.PersonNameDto;
import com.safetynetalerte.fr.safetynetAlerts.exception.AddressNotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.exception.MedicalRecordNotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.service.PersonService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.safetynetalerte.fr.safetynetAlerts.TestData.ROSE;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService service;

    private final ChildDto kendrik = new ChildDto("Kendrik","Stelzer", 12);
    private final PersonNameDto brian = new PersonNameDto("Brian","Stelzer");
    private final PersonNameDto shawna = new PersonNameDto("Shawna","Stelzer");

    @Test
    @DisplayName("GET /childAlert?address=947 E. Rose Dr : 200 et JSON attendu")
    void getchildrenListByAddress_casPassant_retourne200() throws Exception {

        ChildAlertDto dto = new ChildAlertDto(List.of(kendrik), List.of(brian, shawna));
        when(service.getChildrenByaddress(ROSE)).thenReturn(dto);


        mockMvc.perform(get("/childAlert").param("address", ROSE))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.children.length()").value(1))
                .andExpect(jsonPath("$.children[0].firstName").value("Kendrik"))
                .andExpect(jsonPath("$.children[0].lastName").value("Stelzer"))
                .andExpect(jsonPath("$.children[0].age").value(12))
                .andExpect(jsonPath("$.familyMembers.length()").value(2))
                .andExpect(jsonPath("$.familyMembers[0].firstName").value("Brian"))
                .andExpect(jsonPath("$.familyMembers[0].lastName").value("Stelzer"))
                .andExpect(jsonPath("$.familyMembers[1].firstName").value("Shawna"))
                .andExpect(jsonPath("$.familyMembers[1].lastName").value("Stelzer"));


        verify(service).getChildrenByaddress(ROSE);
    }

    @Test
    @DisplayName("GET /childAlert : adresse sans enfant, 200 et deux listes vides")
    void getChildrenListByAddress_sansEnfant_retourne200ListesVides() throws Exception {
        when(service.getChildrenByaddress(ROSE))
                .thenReturn(new ChildAlertDto(List.of(), List.of()));

        mockMvc.perform(get("/childAlert").param("address", ROSE))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.children").isArray())
                .andExpect(jsonPath("$.children").isEmpty())
                .andExpect(jsonPath("$.familyMembers").isArray())
                .andExpect(jsonPath("$.familyMembers").isEmpty());

        verify(service).getChildrenByaddress(ROSE);
    }


    @Test
    @DisplayName("GET /childAlert : plusieurs enfants, l'ordre et les âges sont conservés")
    void getChildrenListByAddress_plusieursEnfants_retourne200() throws Exception {
        ChildDto zach = new ChildDto("Zach", "Stelzer", 6);
        ChildAlertDto dto = new ChildAlertDto(List.of(kendrik, zach), List.of(brian));
        when(service.getChildrenByaddress(ROSE)).thenReturn(dto);

        mockMvc.perform(get("/childAlert").param("address", ROSE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children.length()").value(2))
                .andExpect(jsonPath("$.children[0].age").value(12))
                .andExpect(jsonPath("$.children[1].firstName").value("Zach"))
                .andExpect(jsonPath("$.children[1].age").value(6))
                .andExpect(jsonPath("$.familyMembers.length()").value(1));
    }

    @Test
    @DisplayName("GET /childAlert sans paramètre address : 400 et service jamais appelé")
    void getChildrenListByAddress_addressAbsent_retourne400() throws Exception {
        mockMvc.perform(get("/childAlert"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @DisplayName("GET /childAlert?address=inconnue : 404")
    void getChildrenListByAddress_adresseInconnue_retourne404() throws Exception {
        var ex = new AddressNotFoundException("Unknown St");

        when(service.getChildrenByaddress("Unknown St"))
                .thenThrow(ex);

        mockMvc.perform(get("/childAlert").param("address", "Unknown St"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(ex.getMessage()));

        verify(service).getChildrenByaddress("Unknown St");
    }

    @Test
    @DisplayName("GET /childAlert : dossier médical manquant, 404 et message")
    void getChildrenListByAddress_dossierMedicalManquant_retourne404() throws Exception {
        when(service.getChildrenByaddress(ROSE))
                .thenThrow(new MedicalRecordNotFoundException("Shawna", "Stelzer"));

        mockMvc.perform(get("/childAlert").param("address", ROSE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Missing medical record for : Shawna Stelzer"));

        verify(service).getChildrenByaddress(ROSE);
    }

}
