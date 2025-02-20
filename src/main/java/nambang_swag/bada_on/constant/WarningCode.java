package nambang_swag.bada_on.constant;

import lombok.Getter;

@Getter
public enum WarningCode {
	NONE(0, "없음"),
	STRONG_WIND(1, "강풍"),
	HEAVY_RAIN(2, "호우"),
	COLD_WAVE(3, "한파"),
	DRY_WEATHER(4, "건조"),
	STORM_SURGE(5, "폭풍해일"),
	HIGH_SEAS(6, "풍랑"),
	TYPHOON(7, "태풍"),
	HEAVY_SNOW(8, "대설"),
	YELLOW_DUST(9, "황사"),
	HEAT_WAVE(12, "폭염");

	private final int code;
	private final String description;

	WarningCode(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public static WarningCode from(String description) {
		for (WarningCode code : values()) {
			if (code.getDescription().equals(description)) {
				return code;
			}
		}
		return NONE;
	}
}
