package nambang_swag.bada_on.util.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import nambang_swag.bada_on.util.validation.annotation.ValidLatitude;

public class LatitudeValidator implements ConstraintValidator<ValidLatitude, Double> {

	@Override
	public boolean isValid(Double value, ConstraintValidatorContext context) {
		if (value == null) {
			return false;
		}

		String stringValue = Double.toString(value);
		return stringValue.matches("^\\d{1,2}\\.\\d{3}$") && value >= 30 && value <= 45;
	}
}
