package nambang_swag.bada_on.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DailyWeather {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private int date;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Place place;

	// 일출
	@Column(name = "sun_rise_time")
	private int sunRiseTime;

	// 일몰
	@Column(name = "sun_set_time")
	private int sunSetTime;

	@Builder
	public DailyWeather(int date, Place place) {
		this.date = date;
		this.place = place;
	}

	public void updateSunRiseSetInfo(int sunRiseTime, int sunSetTime) {
		this.sunRiseTime = sunRiseTime;
		this.sunSetTime = sunSetTime;
	}
}
