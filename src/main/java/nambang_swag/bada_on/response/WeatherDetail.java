package nambang_swag.bada_on.response;

import java.util.List;

import nambang_swag.bada_on.entity.Weather;

public record WeatherDetail(
	int date,
	int hour,
	List<WarningDetail> warning,
	String skyCondition,
	float temperature,
	float wind,
	float precipitation,
	float tideInfo,
	float waveHeight,
	List<TideInfo> tideInfoList,
	List<ActivityScore> score
) {
	public static WeatherDetail of(Weather weather, List<WarningDetail> warning, List<TideInfo> tideInfoList,
		List<ActivityScore> score, float tidePercentage) {
		return new WeatherDetail(
			weather.getDate(),
			weather.getTime() / 100,
			warning,
			getSkyCondition(weather),
			weather.getHourlyTemperature(),
			weather.getWindSpeed(),
			weather.getHourlyPrecipitation(),
			tidePercentage,
			weather.getWaveHeight(),
			tideInfoList,
			score
		);
	}

	private static String getSkyCondition(Weather weather) {
		if (weather.getHourlySnowAccumulation() > 0) {
			return "눈";
		} else if (weather.getHourlyPrecipitation() > 0) {
			return "비";
		} else {
			return weather.getSkyCondition().getDescription();
		}
	}
}
