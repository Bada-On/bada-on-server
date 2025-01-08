package nambang_swag.bada_on.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.extern.slf4j.Slf4j;
import nambang_swag.bada_on.constant.PrecipitationType;
import nambang_swag.bada_on.constant.SkyCondition;

@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Weather {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "place_id")
	private Place place;

	private int date;
	private int time;

	// 강수확률 (%)
	@Column(name = "precipitation_probability")
	private int precipitationProbability;

	// 강수형태 (코드값)
	// (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
	// (초단기) 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
	@Column(name = "precipitation_type", nullable = false)
	@Enumerated(value = EnumType.STRING)
	private PrecipitationType precipitationType;

	// 1시간 강수량 (mm)
	@Column(name = "hourly_precipitation", nullable = false)
	private float hourlyPrecipitation;

	// 습도 (%)
	@Column(name = "humidity", nullable = false)
	private int humidity;

	// 1시간 신적설 (cm)
	@Column(name = "hourly_snow_accumulation", nullable = false)
	private float hourlySnowAccumulation;

	// 하늘상태 (코드값)
	// 맑음(1), 구름많음(3), 흐림(4)
	@Column(name = "sky_condition", nullable = false)
	private SkyCondition skyCondition;

	// 1시간 기온 (℃)
	@Column(name = "hourly_temperature", nullable = false)
	private float hourlyTemperature;

	// 파고 (m)
	@Column(name = "wave_height", nullable = false)
	private float waveHeight;

	// 풍향 (deg)
	@Column(name = "wind_direction", nullable = false)
	private float windDirection;

	// 풍속 (m/s)
	@Column(name = "wind_speed", nullable = false)
	private float windSpeed;

	// 수온 (℃)
	private float waterTemperature;

	private boolean isUpdated;

	@Builder
	public Weather(Place place, int date, int time) {
		this.place = place;
		this.date = date;
		this.time = time;

		this.precipitationProbability = -99;
		this.precipitationType = PrecipitationType.NONE;
		this.hourlyPrecipitation = -99;
		this.humidity = -99;
		this.hourlySnowAccumulation = -99;
		this.skyCondition = SkyCondition.NONE;
		this.hourlyTemperature = -99;
		this.waveHeight = -99;
		this.windDirection = -99;
		this.windSpeed = -99;
		this.waterTemperature = -99;
	}

	public void updateWaterTemperature(float temperature) {
		this.waterTemperature = temperature;
	}

	private static final float NO_DATA = -99f;

	public void updateWeatherData(String category, String value) {
		isUpdated = true;
		try {
			switch (category.trim()) {
				case "TMP", "T1H" -> hourlyTemperature = parseFloat(value);
				case "VEC" -> windDirection = parseFloat(value);
				case "WSD" -> windSpeed = parseFloat(value);
				case "SKY" -> skyCondition = SkyCondition.from(parseInt(value));
				case "PTY" -> precipitationType = PrecipitationType.from(parseInt(value));
				case "POP" -> precipitationProbability = parseInt(value);
				case "WAV" -> waveHeight = parseFloat(value);
				case "PCP", "RN1" -> hourlyPrecipitation = parseAccumulation(value, "강수없음");
				case "REH" -> humidity = parseInt(value);
				case "SNO" -> hourlySnowAccumulation = parseHourlySnowAccumulation(value, "적설없음");
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Failed to parse value: " + value + " for category: " + category, e);
		}
	}

	private float parseFloat(String value) {
		return Float.parseFloat(value);
	}

	private int parseInt(String value) {
		return Integer.parseInt(value);
	}

	private float parseAccumulation(String value, String noneString) {
		if (value.equals(noneString)) {
			return NO_DATA;
		}
		return parseFloat(value.split("mm")[0]);
	}

	private float parseHourlySnowAccumulation(String value, String noneString) {
		if (value.equals(noneString)) {
			return NO_DATA;
		}

		if (value.endsWith("cm미만")) {
			String numericPart = value.substring(0, value.indexOf("cm"));
			return Float.parseFloat(numericPart); // Convert cm to mm
		}

		return parseFloat(value.split("cm")[0]);
	}
}
