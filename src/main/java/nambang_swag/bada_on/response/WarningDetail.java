package nambang_swag.bada_on.response;

import java.time.LocalDateTime;

import nambang_swag.bada_on.entity.Warning;

public record WarningDetail(
	String title,
	String location,
	String description,
	LocalDateTime startTime,
	LocalDateTime endTime
) {
	public static WarningDetail from(Warning warning) {
		return new WarningDetail(
			warning.getCode().getDescription() + warning.getLevel().getDescription(),
			warning.getRegion().getName(),
			warning.getCode().getDescription() + warning.getLevel().getDescription() + "가 발효 중입니다. 바다 활동을 삼가해주세요.",
			warning.getIssuedAt(),
			warning.getLiftedAt()
		);
	}
}
