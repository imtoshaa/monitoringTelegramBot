package by.telegram.monitoring.bot.utils;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SerialNumberValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SerialNumberConstraint {

  String message() default "Invalid login";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
