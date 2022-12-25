package by.telegram.monitoring.bot.utils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static by.telegram.monitoring.bot.utils.constants.PatternConstants.PASSWORD_PATTERN;

public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {

  private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);


  @Override
  public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
    Matcher matcher = pattern.matcher(password);
    return matcher.matches();
  }
}
