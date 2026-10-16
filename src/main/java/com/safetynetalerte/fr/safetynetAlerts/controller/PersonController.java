package com.safetynetalerte.fr.safetynetAlerts.controller;

import com.safetynetalerte.fr.safetynetAlerts.dto.ChildAlertDto;
import com.safetynetalerte.fr.safetynetAlerts.exception.NotFoundException;
import com.safetynetalerte.fr.safetynetAlerts.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PersonController {

    private final PersonService service;

    public PersonController(PersonService service) {
        this.service = service;
    }

    @GetMapping("/childAlert")
    public ChildAlertDto getchildrenListByAddress(@RequestParam("address") String address)
    {
        try {
            log.info("GET /childAlert?address={}", address);

            ChildAlertDto response = service.getChildrenByaddress(address);
            log.info("ServiceResponse from controller: {} enfants, {} adultes,",
                    response.children().size(), response.familyMembers().size());
            return response;
        } catch (NotFoundException e) {
            log.error( e.getMessage());
            throw e;
        }
    }
}
