package nambang_swag.bada_on.entity;

import lombok.Getter;

@Getter
public enum WarningStatus {
	NONE("없음"),
	ISSUED("발표"),
	MODIFIED("변경"),
	LIFTED("해제");

	private final String description;

	WarningStatus(String description) {
		this.description = description;
	}

	public static WarningStatus from(String description) {
		for (WarningStatus status : values()) {
			if (status.getDescription().equals(description)) {
				return status;
			}
		}
		return NONE;
	}
}
