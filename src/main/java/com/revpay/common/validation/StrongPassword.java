package com.revpay.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
@Documented
public @interface StrongPassword {
    String message() default "Password must be at least 8 characters long, contain a digit, an uppercase letter, and a special character";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
