package nambang_swag.bada_on.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AvailableTimeRequest(
	@NotBlank
	@Pattern(
		regexp = "^\\d{4}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])$",
		message = "yyyyMMdd 형태로 요청해주세요."
	)
	String date
) {
}
