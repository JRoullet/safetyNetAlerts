package com.safetynetalerte.fr.safetynetAlerts.exception;

public class MedicalRecordNotFoundException extends NotFoundException {

  public MedicalRecordNotFoundException(String firstName, String lastName) {
    super("Missing medical record for : " + firstName + " " + lastName);
  }

}
