package nambang_swag.bada_on.entity;

import static jakarta.persistence.GenerationType.*;
import static nambang_swag.bada_on.constant.Activity.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nambang_swag.bada_on.constant.Activity;
import nambang_swag.bada_on.constant.WarningRegion;

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

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private WarningRegion landRegion;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private WarningRegion seaRegion;

	@Column(nullable = false)
	private boolean canSnorkeling;

	@Column(nullable = false)
	private boolean canDiving;

	@Column(nullable = false)
	private boolean canSwimming;

	@Column(nullable = false)
	private boolean canSurfing;

	@Column(nullable = false)
	private boolean canPaddling;

	@Builder
	public Place(String name, Double latitude, Double longitude, String address, int nx, int ny,
		WarningRegion landRegion, WarningRegion seaRegion) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.nx = nx;
		this.ny = ny;
		this.landRegion = landRegion;
		this.seaRegion = seaRegion;
	}

	public void updateActivityStatus(Activity activity, boolean isEnabled) {
		switch (activity) {
			case SNORKELING -> canSnorkeling = isEnabled;
			case DIVING -> canDiving = isEnabled;
			case SWIMMING -> canSwimming = isEnabled;
			case SURFING -> canSurfing = isEnabled;
			case PADDlING -> canPaddling = isEnabled;
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
		if (this.canPaddling) {
			activities.add(PADDlING.getValue());
		}
		return activities;
	}
}
