package nambang_swag.bada_on.util.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nambang_swag.bada_on.util.validation.annotation.ValidLongitude;

public class LongitudeValidator implements ConstraintValidator<ValidLongitude, Double> {
	@Override
	public boolean isValid(Double value, ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}
		String stringValue = String.valueOf(value);
		return stringValue.matches("^\\d{3}\\.\\d{3}$") && value >= 120 && value <= 140;
	}
}
