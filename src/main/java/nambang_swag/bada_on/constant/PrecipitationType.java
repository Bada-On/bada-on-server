package nambang_swag.bada_on.constant;

import lombok.Getter;

@Getter
public enum PrecipitationType {
	NONE(0, "없음"),
	RAIN(1, "비"),
	RAIN_SNOW(2, "비/눈"),
	SNOW(3, "눈"),
	SHOWER(4, "소나기"),
	RAIN_DROPS(5, "빗방울"),
	RAIN_SNOW_DROPS(6, "빗방울눈날림"),
	SNOW_FLURRY(7, "눈날림");

	private final int code;
	private final String description;

	PrecipitationType(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public static PrecipitationType from(int code) {
		for (PrecipitationType type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		return NONE;
	}
}
