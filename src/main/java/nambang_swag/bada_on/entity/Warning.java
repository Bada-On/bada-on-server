package nambang_swag.bada_on.entity;

import static jakarta.persistence.EnumType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nambang_swag.bada_on.constant.WarningCode;
import nambang_swag.bada_on.constant.WarningLevel;
import nambang_swag.bada_on.constant.WarningRegion;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Warning {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	private WarningStatus status;

	@Column(nullable = false)
	@Enumerated(value = STRING)
	private WarningRegion region;

	@Column(nullable = false)
	@Enumerated(value = STRING)
	private WarningCode code;

	@Column(nullable = false)
	@Enumerated(value = STRING)
	private WarningLevel level;

	// 발표시작
	private LocalDateTime issuedAt;

	// 해제(예고)시각
	private LocalDateTime liftedAt;

	@Builder
	public Warning(WarningStatus status, WarningRegion region, WarningCode code, WarningLevel level,
		LocalDateTime issuedAt, LocalDateTime liftedAt) {
		this.status = status;
		this.region = region;
		this.code = code;
		this.level = level;
		this.issuedAt = issuedAt;
		this.liftedAt = liftedAt;
	}

	public void warningModified() {
		this.status = WarningStatus.MODIFIED;
	}

	public void warningLifted(LocalDateTime liftedAt) {
		this.status = WarningStatus.LIFTED;
		this.liftedAt = liftedAt;
	}

	public String getWarningMessage() {
		return this.code.getDescription() + this.level.getDescription();
	}
}
