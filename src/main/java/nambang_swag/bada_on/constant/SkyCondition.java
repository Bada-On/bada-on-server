package nambang_swag.bada_on.constant;

import lombok.Getter;

@Getter
public enum SkyCondition {
	NONE(0, "없음"),
	SUNNY(1, "맑음"),
	CLOUDS(3, "흐림"), // 구름 많음
	CLOUDY(4, "흐림");

	private final int code;
	private final String description;

	SkyCondition(int code, String description) {
		this.code = code;
		this.description = description;
	}

	public static SkyCondition from(int code) {
		for (SkyCondition type : values()) {
			if (type.code == code) {
				return type;
			}
		}
		throw new IllegalArgumentException("Invalid precipitation type code: " + code);
	}
}
