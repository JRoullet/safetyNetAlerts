package com.safetynetalerte.fr.safetynetAlerts.dto;

import java.util.List;

public record FireStationCoverageDto(List<PersonDto> people, long adultCount, long childCount) {
}
