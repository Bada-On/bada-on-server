package nambang_swag.bada_on.response;

import java.util.List;

import nambang_swag.bada_on.entity.Weather;

public record WeatherSummary(
	int date,
	int hour,
	List<String> warning,
	List<String> recommendActivity,
	String skyCondition,
	float temperature,
	String wind,
	float tideHeight,
	float waveHeight
) {
	public static WeatherSummary of(Weather weather, List<String> warning, List<String> recommendActivities,
		float tideHeight) {
		return new WeatherSummary(
			weather.getDate(),
			weather.getTime() / 100,
			warning,
			recommendActivities,
			getSkyCondition(weather),
			weather.getHourlyTemperature(),
			getWindString(weather.getWindSpeed()),
			tideHeight,
			weather.getWaveHeight()
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

	private static String getWindString(float windSpeed) {
		if (windSpeed >= 0f && windSpeed < 2f) {
			return "매우 약함";
		} else if (windSpeed >= 2f && windSpeed < 4f) {
			return "약함";
		} else if (windSpeed >= 4f && windSpeed < 7f) {
			return "적당";
		} else if (windSpeed >= 7f && windSpeed < 10f) {
			return "강함";
		} else {
			return "매우 강함";
		}
	}
}
