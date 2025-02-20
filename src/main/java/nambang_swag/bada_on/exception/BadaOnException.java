package nambang_swag.bada_on.exception;

import lombok.Getter;

@Getter
public abstract class BadaOnException extends RuntimeException {

	public BadaOnException(String message) {
		super(message);
	}

	public abstract int getStatusCode();
}
