package com.safetynetalerte.fr.safetynetAlerts.dto;

import lombok.Data;

@Data
public class PersonNameDto{
    String firstName;
    String lastName;

    public PersonNameDto(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public PersonNameDto() {
    }
}
