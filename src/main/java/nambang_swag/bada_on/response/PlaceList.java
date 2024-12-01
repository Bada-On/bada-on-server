package nambang_swag.bada_on.response;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PlaceList {

	private int total;
	private List<PlaceInfo> places;

	@Builder
	public PlaceList(int total, List<PlaceInfo> places) {
		this.total = total;
		this.places = places != null ? places : new ArrayList<>();
	}
}
