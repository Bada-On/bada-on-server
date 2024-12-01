package nambang_swag.bada_on.util.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import nambang_swag.bada_on.util.validation.validator.LongitudeValidator;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LongitudeValidator.class)
public @interface ValidLongitude {
	String message() default "유효하지 않은 경도 형식 또는 범위입니다";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
