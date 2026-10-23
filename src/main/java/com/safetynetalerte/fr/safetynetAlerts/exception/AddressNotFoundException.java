package com.safetynetalerte.fr.safetynetAlerts.exception;

public class AddressNotFoundException extends NotFoundException {

  public AddressNotFoundException(String address) {
    super("No address was found at : " + address);
  }

}
