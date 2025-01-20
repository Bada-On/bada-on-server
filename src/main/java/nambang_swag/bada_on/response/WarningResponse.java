package nambang_swag.bada_on.response;

import java.util.List;

public record WarningResponse(
	Long placeId,
	List<String> warning
) {
}
