package nambang_swag.bada_on.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import nambang_swag.bada_on.constant.PrecipitationType;
import nambang_swag.bada_on.entity.TideRecord;
import nambang_swag.bada_on.entity.Weather;

@Getter
public class WeatherDetail {
	private Long id;
	private int date;
	private int time;

	// 강수확률 (%)
	private int precipitationProbability;

	// 강수형태 (코드값)
	// (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
	// (초단기) 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
	private String precipitationType;

	// 1시간 강수량 (mm)
	private float hourlyPrecipitation;

	// 습도 (%)
	private int humidity;

	// 1시간 신적설 (cm)
	private float hourlySnowAccumulation;

	// 하늘 상태
	private String skyCondition;

	// 1시간 기온 (℃)
	private float hourlyTemperature;

	// 파고 (m)
	private float waveHeight;

	// 풍향 (deg)
	private float windDirection;

	// 풍속 (m/s)
	private float windSpeed;

	// 수온 (℃)
	private float waterTemperature;

	private List<TideInfo> tideInfoList;

	@Builder
	public WeatherDetail(Weather weather, List<TideRecord> tideRecordList) {
		this.id = weather.getId();
		this.date = weather.getDate();
		this.time = weather.getTime();
		this.precipitationProbability = weather.getPrecipitationProbability();
		this.precipitationType = weather.getPrecipitationType().getDescription();
		this.hourlyPrecipitation = weather.getHourlyPrecipitation();
		this.humidity = weather.getHumidity();
		this.hourlySnowAccumulation = weather.getHourlySnowAccumulation();
		this.skyCondition = weather.getSkyCondition().getDescription();
		this.hourlyTemperature = weather.getHourlyTemperature();
		this.waveHeight = weather.getWaveHeight();
		this.windDirection = weather.getWindDirection();
		this.windSpeed = weather.getWindSpeed();
		this.waterTemperature = weather.getWaterTemperature();
		this.tideInfoList = tideRecordList.stream()
			.map(tideRecord -> TideInfo.builder()
				.code(tideRecord.getCode())
				.tidalTime(tideRecord.getTidalTime())
				.tidalLevel(tideRecord.getTidalLevel())
				.build()
			).toList();
	}

	@Getter
	public static class TideInfo {
		private int tidalLevel;
		private LocalDateTime tidalTime;
		private String code;

		@Builder
		public TideInfo(int tidalLevel, LocalDateTime tidalTime, String code) {
			this.tidalLevel = tidalLevel;
			this.tidalTime = tidalTime;
			this.code = code;
		}
	}
}
