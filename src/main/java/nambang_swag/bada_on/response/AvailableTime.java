package nambang_swag.bada_on.response;

import java.util.List;

public record AvailableTime(
	int date,
	List<Integer> hours
) {
}
