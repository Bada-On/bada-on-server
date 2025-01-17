package nambang_swag.bada_on.entity;

import static jakarta.persistence.GenerationType.*;
import static nambang_swag.bada_on.constant.Activity.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nambang_swag.bada_on.constant.Activity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Place {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private int nx;

	@Column(nullable = false)
	private int ny;

	private boolean canSnorkeling;
	private boolean canDiving;
	private boolean canSwimming;
	private boolean canSurfing;
	private boolean canPadding;

	@Builder
	public Place(String name, Double latitude, Double longitude, String address, int nx, int ny) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.nx = nx;
		this.ny = ny;
	}

	public void updateActivityStatus(Activity activity, boolean isEnabled) {
		switch (activity) {
			case SNORKELING -> canSnorkeling = isEnabled;
			case DIVING -> canDiving = isEnabled;
			case SWIMMING -> canSwimming = isEnabled;
			case SURFING -> canSurfing = isEnabled;
			case PADDING -> canPadding = isEnabled;
		}
	}

	public List<String> getStringActivities() {
		List<String> activities = new ArrayList<>();
		if (this.canDiving) {
			activities.add(DIVING.getValue());
		}
		if (this.canSnorkeling) {
			activities.add(SNORKELING.getValue());
		}
		if (this.canSwimming) {
			activities.add(SWIMMING.getValue());
		}
		if (this.canSurfing) {
			activities.add(SURFING.getValue());
		}
		if (this.canPadding) {
			activities.add(PADDING.getValue());
		}
		return activities;
	}
}
