package nambang_swag.bada_on.response;

import java.util.List;

import nambang_swag.bada_on.entity.Place;

public record PlaceInfo(
	Long id,
	String name,
	String address,
	Double latitude,
	Double longitude,
	List<String> activities
) {
	public static PlaceInfo from(Place place) {
		return new PlaceInfo(
			place.getId(),
			place.getName(),
			place.getAddress(),
			place.getLatitude(),
			place.getLongitude(),
			place.getStringActivities());
	}
}
