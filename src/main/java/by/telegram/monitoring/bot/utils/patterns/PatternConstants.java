package by.telegram.monitoring.bot.utils.patterns;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PatternConstants {

  public static final String SERIAL_NUMBER_PATTERN = "^\\d{5}$";
  public static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,100})";
}
