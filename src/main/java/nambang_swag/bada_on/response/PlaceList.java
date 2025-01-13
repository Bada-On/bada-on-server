package nambang_swag.bada_on.response;

import java.util.List;

public record PlaceList(
	int total,
	List<PlaceInfo> places
) {
	public static PlaceList of(int total, List<PlaceInfo> places) {
		return new PlaceList(total, places);
	}
}
