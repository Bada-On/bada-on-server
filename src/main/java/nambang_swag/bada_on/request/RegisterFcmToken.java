package nambang_swag.bada_on.request;

import lombok.Getter;

@Getter
public class RegisterFcmToken {
	private String token;
	private String device;
	private String timeStamp;
}
