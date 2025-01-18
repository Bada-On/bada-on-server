package nambang_swag.bada_on.constant;

import lombok.Getter;

@Getter
public enum WarningRegion {
	NONE("없음"),
	JEJU_NORTHERN("제주도북부"),
	JEJU_SOUTHERN("제주도남부"),
	JEJU_EASTERN("제주도동부"),
	JEJU_WESTERN("제주도서부"),
	NORTHERN_COASTAL_SEA("북부연안바다"),
	NORTHWESTERN_COASTAL_SEA("북서연안바다"),
	SOUTHWESTERN_COASTAL_SEA("남서연안바다"),
	GAPADO_COASTAL_SEA("가파도연안바다"),
	SOUTHERN_COASTAL_SEA("남부연안바다"),
	SOUTHEASTERN_COASTAL_SEA("남동연안바다"),
	UDO_COASTAL_SEA("우도연안바다"),
	NORTHEASTERN_COASTAL_SEA("북동연안바다");

	private final String name;

	WarningRegion(String name) {
		this.name = name;
	}

	public static boolean contains(String test) {
		for (WarningRegion region : WarningRegion.values()) {
			if (region.name.equals(test)) {
				return true;
			}
		}
		return false;
	}

	public static WarningRegion from(String name) {
		for (WarningRegion region : values()) {
			if (region.getName().equals(name)) {
				return region;
			}
		}
		return NONE;
	}
}
