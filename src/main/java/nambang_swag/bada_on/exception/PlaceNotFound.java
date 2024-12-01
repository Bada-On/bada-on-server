package nambang_swag.bada_on.exception;

public class PlaceNotFound extends BadaOnException {

	private static final String MESSAGE = "등록되지 않은 장소입니다.";

	public PlaceNotFound() {
		super(MESSAGE);
	}

	@Override
	public int getStatusCode() {
		return 404;
	}
}
