package com.safetynetalerte.fr.safetynetAlerts.dto;

import java.util.List;

public record ResidentDto(String firstName, String lastName, String phone, int age, List<String> medications, List<String> allergies) {
}
