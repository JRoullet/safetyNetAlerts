package com.safetynetalerte.fr.safetynetAlerts.dto;

import java.util.List;

public record FireAlertDto(int stationNumber, List<ResidentDto> residents) {}
