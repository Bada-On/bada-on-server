package nambang_swag.bada_on.constant;

import lombok.Getter;

@Getter
public enum WarningLevel {
	NONE("없음"),
	ADVISORY("주의보"),
	WARNING("경보");

	private final String description;

	WarningLevel(String description) {
		this.description = description;
	}
}
