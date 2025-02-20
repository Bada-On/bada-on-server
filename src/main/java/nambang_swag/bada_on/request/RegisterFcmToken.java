package nambang_swag.bada_on.request;

public record RegisterFcmToken(
	String token,
	String device,
	String timeStamp
) {
}
