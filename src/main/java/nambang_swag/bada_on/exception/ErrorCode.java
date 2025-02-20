package nambang_swag.bada_on.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
	// Common
	INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "Internal server error"),
	INVALID_INPUT_VALUE(400, "INVALID_INPUT_VALUE", "Invalid input value"),
	INVALID_TYPE_VALUE(400, "INVALID_TYPE_VALUE", "Invalid type value");
	private final int status;
	private final String code;
	private final String message;

	ErrorCode(int status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}