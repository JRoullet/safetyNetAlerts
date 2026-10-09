package com.safetynetalerte.fr.safetynetAlerts.controller;

import com.safetynetalerte.fr.safetynetAlerts.dto.FireStationCoverageDto;
import com.safetynetalerte.fr.safetynetAlerts.service.FireStationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class FireStationController {

    private final FireStationService service;

    public FireStationController(FireStationService service) {
        this.service = service;
    }

    @GetMapping("/firestation")
    public FireStationCoverageDto getPeopleByStation(
            @RequestParam("stationNumber") int stationNumber
    ){
        log.info("GET /firestation?stationNumber={}", stationNumber);

        FireStationCoverageDto response = service.getPeopleCoveredByStation(stationNumber);

        log.info("ServiceResponse from controller: {} people, {} adults, {} children",
                response.people().size(), response.adultCount(), response.childCount());
        return response;
    }
}
