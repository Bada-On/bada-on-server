package nambang_swag.bada_on.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nambang_swag.bada_on.constant.TideObservatory;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class TideRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private int date;

	@Enumerated(value = EnumType.STRING)
	private TideObservatory tideObservatory;

	private int tidalLevel;
	private LocalDateTime tidalTime;
	private String code;

	@Builder
	public TideRecord(int date, TideObservatory tideObservatory) {
		this.date = date;
		this.tideObservatory = tideObservatory;
	}

	public void updateForecastData(TideObservatory tideObservatory, int tidalLevel, LocalDateTime tidalTime,
		String code) {
		this.tideObservatory = tideObservatory;
		this.tidalLevel = tidalLevel;
		this.tidalTime = tidalTime;
		this.code = code;
	}
}
