package com.safetynetalerte.fr.safetynetAlerts.dto;

import lombok.Data;

@Data
public class ChildDto extends PersonNameDto{
    Integer age;

    public ChildDto(String firstName, String lastName, Integer age) {
        super(firstName, lastName);
        this.age = age;
    }
}
