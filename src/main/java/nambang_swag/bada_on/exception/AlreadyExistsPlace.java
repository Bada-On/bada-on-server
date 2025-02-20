package nambang_swag.bada_on.exception;

public class AlreadyExistsPlace extends BadaOnException {

	private static final String MESSAGE = "이미 등록된 장소입니다.";

	public AlreadyExistsPlace() {
		super(MESSAGE);
	}

	@Override
	public int getStatusCode() {
		return 400;
	}
}
