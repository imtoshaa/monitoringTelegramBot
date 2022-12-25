package by.telegram.monitoring.bot.utils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static by.telegram.monitoring.bot.utils.constants.PatternConstants.SERIAL_NUMBER_PATTERN;

public class SerialNumberValidator implements ConstraintValidator<SerialNumberConstraint, String> {

  private static final Pattern pattern = Pattern.compile(SERIAL_NUMBER_PATTERN);


  @Override
  public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
    Matcher matcher = pattern.matcher(value);
    return matcher.matches();
  }
}
