package nambang_swag.bada_on.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import nambang_swag.bada_on.exception.BadaOnException;
import nambang_swag.bada_on.response.ErrorResponse;

@ControllerAdvice
public class ExceptionController {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> invalidRequestHandler(MethodArgumentNotValidException e) {
		ErrorResponse body = ErrorResponse.builder()
			.code("400")
			.message("잘못된 요청입니다.")
			.build();

		for (FieldError fieldError : e.getFieldErrors()) {
			body.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
		}

		return ResponseEntity.status(400).body(body);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
		HttpRequestMethodNotSupportedException e) {
		ErrorResponse body = ErrorResponse.builder()
			.code("400")
			.message("지원하지 않는 메서드입니다.")
			.build();
		return ResponseEntity.status(400).body(body);
	}

	@ExceptionHandler(BadaOnException.class)
	public ResponseEntity<ErrorResponse> badaOnExceptionHandler(BadaOnException e) {
		int statusCode = e.getStatusCode();

		ErrorResponse body = ErrorResponse.builder()
			.code(String.valueOf(statusCode))
			.message(e.getMessage())
			.build();

		return ResponseEntity.status(statusCode).body(body);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
		ErrorResponse body = ErrorResponse.builder()
			.code("500")
			.message(e.getMessage())
			.build();
		return ResponseEntity.status(500).body(body);
	}
}
