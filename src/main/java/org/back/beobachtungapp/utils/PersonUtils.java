package org.back.beobachtungapp.utils;

import java.time.LocalDate;
import java.time.Period;

public class PersonUtils {
  public static int calculateAge(LocalDate birthDate) {
    if (birthDate == null) {
      throw new IllegalArgumentException("Dob can't be null");
    }
    return Period.between(birthDate, LocalDate.now()).getYears();
  }
}
