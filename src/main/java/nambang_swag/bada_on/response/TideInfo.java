package nambang_swag.bada_on.response;

import java.time.LocalDateTime;

public record TideInfo(
	int tidalLevel,
	LocalDateTime tidalTime,
	String code
) {
}
