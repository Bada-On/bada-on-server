package nambang_swag.bada_on.exception;

public class BadaOnCustomException extends RuntimeException {

	private final ErrorCode errorCode;

	public BadaOnCustomException(String message, ErrorCode errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public BadaOnCustomException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}

