package nambang_swag.bada_on.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import nambang_swag.bada_on.entity.Place;

@Getter
@JsonPropertyOrder({"id", "name", "latitude", "longitude", "activities"})
public class PlaceInfo {
	private Long id;
	private String name;
	private Double latitude;
	private Double longitude;
	private List<String> activities;

	@Builder
	public PlaceInfo(Place place, List<String> activities) {
		this.id = place.getId();
		this.name = place.getName();
		this.latitude = place.getLatitude();
		this.longitude = place.getLongitude();
		this.activities = Objects.requireNonNullElseGet(activities, ArrayList::new);
	}

}
