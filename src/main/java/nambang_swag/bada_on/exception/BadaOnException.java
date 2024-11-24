package nambang_swag.bada_on.exception;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public abstract class BadaOnException extends RuntimeException {

	public BadaOnException(String message) {
		super(message);
	}

	public abstract int getStatusCode();
}
