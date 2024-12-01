package nambang_swag.bada_on.exception;

public class WeatherNotFound extends BadaOnException {

	private static final String MESSAGE = "기상 데이터가 존재하지 않습니다.";

	public WeatherNotFound() {
		super(MESSAGE);
	}

	@Override
	public int getStatusCode() {
		return 404;
	}
}
