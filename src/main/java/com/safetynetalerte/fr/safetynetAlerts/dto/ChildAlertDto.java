package com.safetynetalerte.fr.safetynetAlerts.dto;

import java.util.List;

public record ChildAlertDto(List<ChildDto> children, List<PersonNameDto> familyMembers) {
}
