package com.safetynetalerte.fr.safetynetAlerts.exception;

public class FirestationNotFoundException extends NotFoundException {

  public FirestationNotFoundException(int firestation) {
    super("No firestation was found for id : " + firestation);
  }

  public FirestationNotFoundException(String address) {
    super("No firestation was found for address : " + address);
  }
}
