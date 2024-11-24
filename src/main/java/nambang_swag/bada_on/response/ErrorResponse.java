package nambang_swag.bada_on.response;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ErrorResponse {

	private final String code;
	private final String message;
	private final Map<String, String> validation;

	@Builder
	public ErrorResponse(String code, String message, Map<String, String> validation) {
		this.code = code;
		this.message = message;
		this.validation = validation;
	}

	public void addValidation(String fieldName, String errorMessage) {
		this.validation.put(fieldName, errorMessage);
	}
}
