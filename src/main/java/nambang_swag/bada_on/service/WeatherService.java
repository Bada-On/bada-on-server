package nambang_swag.bada_on.service;

import static nambang_swag.bada_on.constant.Activity.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nambang_swag.bada_on.constant.Activity;
import nambang_swag.bada_on.constant.PrecipitationType;
import nambang_swag.bada_on.constant.SkyCondition;
import nambang_swag.bada_on.constant.TideObservatory;
import nambang_swag.bada_on.entity.Place;
import nambang_swag.bada_on.entity.TideRecord;
import nambang_swag.bada_on.entity.Weather;
import nambang_swag.bada_on.exception.PlaceNotFound;
import nambang_swag.bada_on.exception.WeatherNotFound;
import nambang_swag.bada_on.repository.PlaceRepository;
import nambang_swag.bada_on.repository.TideRepository;
import nambang_swag.bada_on.repository.WeatherRepository;
import nambang_swag.bada_on.response.AvailableTime;
import nambang_swag.bada_on.response.WeatherDetail;
import nambang_swag.bada_on.response.WeatherSummary;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class WeatherService {

	private final WeatherRepository weatherRepository;
	private final TideRepository tideRepository;
	private final PlaceRepository placeRepository;

	public WeatherSummary getWeatherSummary(Long id, int date, int hour) {
		Place place = placeRepository.findById(id).orElseThrow(PlaceNotFound::new);
		Weather weather = weatherRepository.findByDateAndTimeAndPlace(date, hour * 100, place)
			.orElseThrow(WeatherNotFound::new);

		List<Integer> relevantDates = calculateRelevantDates();
		TideObservatory tideObservatory = TideObservatory.findNearest(place.getLatitude(), place.getLongitude());
		List<TideRecord> tideRecords = tideRepository.findAllByDatesAndTideObservatory(relevantDates, tideObservatory);

		int tidePercentage = calculateTidePercentage(tideRecords);
		return WeatherSummary.of(
			weather,
			new ArrayList<>(),
			getRecommendActivity(weather, tideRecords),
			tidePercentage
		);
	}

	public List<WeatherDetail> getWeatherDetail(Long placeId) {
		Place place = placeRepository.findById(placeId).orElseThrow(PlaceNotFound::new);

		LocalDateTime now = LocalDateTime.now();
		int date = Integer.parseInt(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
		int time = Integer.parseInt(now.format(DateTimeFormatter.ofPattern("HH00")));

		List<WeatherDetail> result = new ArrayList<>();
		List<Weather> weatherForDetails = weatherRepository.findWeatherByPlaceIdWithDateGreaterThan(placeId, date);
		for (Weather weather : weatherForDetails) {
			if (!weather.isUpdated() || (weather.getDate() <= date && weather.getTime() < time)) {
				continue;
			}

			List<TideRecord> tideRecords = tideRepository.findAllByDateAndTideObservatory(date,
				TideObservatory.findNearest(place.getLatitude(), place.getLongitude()));

			WeatherDetail weatherDetail = WeatherDetail.builder()
				.weather(weather)
				.tideRecordList(tideRecords)
				.build();
			result.add(weatherDetail);
		}

		return result;
	}

	private Activity getRecommendActivity(Weather weather, List<TideRecord> tideRecords) {
		Map<Activity, Integer> activityScores = new HashMap<>();

		activityScores.put(SNORKELING, calculateSnorkelingScore(weather, tideRecords));
		activityScores.put(SWIMMING, calculateSwimmingScore(weather, tideRecords));
		activityScores.put(DIVING, calculateDivingScore(weather, tideRecords));
		activityScores.put(KAYAKING_AND_PADDLE_BOARDING, calculateKayakingPaddleBoardingScore(weather, tideRecords));
		activityScores.put(SURFING, calculateSurfingScore(weather, tideRecords));

		return activityScores.entrySet().stream()
			.max(Map.Entry.comparingByValue())
			.orElseThrow(() -> new IllegalStateException("No activity scores available"))
			.getKey();
	}

	// Calculate Point
	private int calculateSnorkelingScore(Weather weather, List<TideRecord> tideRecords) {
		int totalScore = 0;

		SkyCondition skyCondition = weather.getSkyCondition();
		if (weather.getPrecipitationType() == PrecipitationType.RAIN
			|| weather.getPrecipitationType() == PrecipitationType.SHOWER
			|| weather.getPrecipitationType() == PrecipitationType.RAIN_DROPS
			|| weather.getPrecipitationType() == PrecipitationType.RAIN_SNOW_DROPS) {
			totalScore += 10;
		} else if (weather.getPrecipitationType() == PrecipitationType.SNOW
			|| weather.getPrecipitationType() == PrecipitationType.SNOW_FLURRY) {
			totalScore += 5;
		} else if (skyCondition == SkyCondition.SUNNY) {
			totalScore += 20;
		} else if (skyCondition == SkyCondition.CLOUDS || skyCondition == SkyCondition.CLOUDY) {
			totalScore += 15;
		}

		// 기온
		float hourlyTemperature = weather.getHourlyTemperature();
		if (hourlyTemperature >= 24f && hourlyTemperature <= 28f) {
			totalScore += 10; // 매우 적합
		} else if ((hourlyTemperature >= 20f && hourlyTemperature <= 23f) ||
			(hourlyTemperature >= 29f && hourlyTemperature <= 30f)) {
			totalScore += 7;  // 적합
		} else if ((hourlyTemperature >= 16f && hourlyTemperature <= 19f) ||
			hourlyTemperature >= 31f) {
			totalScore += 4;  // 주의
		} else if (hourlyTemperature <= 15f) {
			totalScore += 0;  // 부적합
		}

		// 풍속
		float windSpeed = weather.getWindSpeed();
		if (windSpeed >= 0f && windSpeed < 2f) {
			totalScore += 20; // 매우 약함 (매우 적합)
		} else if (windSpeed >= 2f && windSpeed < 4f) {
			totalScore += 15; // 약함 (적합)
		} else if (windSpeed >= 4f && windSpeed < 7f) {
			totalScore += 10; // 적당 (보통)
		} else if (windSpeed >= 7f && windSpeed < 10f) {
			totalScore += 5;  // 강함 (주의)
		}

		// 강수량
		float hourlyPrecipitation = weather.getHourlyPrecipitation();
		if (hourlyPrecipitation == 0f || hourlyPrecipitation == -99f) {
			totalScore += 10;
		} else if (hourlyPrecipitation >= 1f && hourlyPrecipitation < 8f) {
			totalScore += 7;
		} else if (hourlyPrecipitation >= 8f && hourlyPrecipitation < 11f) {
			totalScore += 5;
		} else if (hourlyPrecipitation >= 11f && hourlyPrecipitation < 20f) {
			totalScore += 2;
		}

		// 물때
		int tidePercentage = calculateTidePercentage(tideRecords);
		if (tidePercentage >= 50 && tidePercentage < 70) {
			totalScore += 20; // 매우 적합
		} else if ((tidePercentage >= 30 && tidePercentage < 50) || (tidePercentage >= 70 && tidePercentage < 90)) {
			totalScore += 15; // 적합
		} else if ((tidePercentage >= 10 && tidePercentage < 30) || (tidePercentage >= 90 && tidePercentage < 100)) {
			totalScore += 10; // 주의
		} else if (tidePercentage <= 10 || tidePercentage == 100) {
			totalScore += 0; // 부적합
		}

		// 수온
		float waterTemperature = weather.getWaterTemperature();
		if (waterTemperature >= 26f && waterTemperature <= 30f) {
			totalScore += 10;
		} else if (waterTemperature >= 24f && waterTemperature < 26f) {
			totalScore += 8;
		} else if (waterTemperature >= 20f && waterTemperature < 24f) {
			totalScore += 5;
		}

		// 파고
		float waveHeight = weather.getWaveHeight();
		if (waveHeight >= 0f && waveHeight < 0.3f) {
			totalScore += 20; // 매우 안정 (매우 적합)
		} else if (waveHeight >= 0.3f && waveHeight < 0.7f) {
			totalScore += 15; // 안정적 (적합)
		} else if (waveHeight >= 0.7f && waveHeight < 1.3f) {
			totalScore += 10; // 주의 (보통)
		} else if (waveHeight >= 1.3f && waveHeight < 2f) {
			totalScore += 5;  // 위험 (주의)
		}
		return totalScore;
	}

	private int calculateDivingScore(Weather weather, List<TideRecord> tideRecords) {
		int totalScore = 0;

		SkyCondition skyCondition = weather.getSkyCondition();
		if (weather.getPrecipitationType() == PrecipitationType.RAIN
			|| weather.getPrecipitationType() == PrecipitationType.SHOWER
			|| weather.getPrecipitationType() == PrecipitationType.RAIN_DROPS
			|| weather.getPrecipitationType() == PrecipitationType.RAIN_SNOW_DROPS) {
			totalScore += 10;
		} else if (weather.getPrecipitationType() == PrecipitationType.SNOW
			|| weather.getPrecipitationType() == PrecipitationType.SNOW_FLURRY) {
			totalScore += 5;
		} else if (skyCondition == SkyCondition.SUNNY) {
			totalScore += 20;
		} else if (skyCondition == SkyCondition.CLOUDS || skyCondition == SkyCondition.CLOUDY) {
			totalScore += 15;
		}

		// 기온
		float hourlyTemperature = weather.getHourlyTemperature();
		if (hourlyTemperature >= 24f && hourlyTemperature <= 28f) {
			totalScore += 10; // 매우 적합
		} else if ((hourlyTemperature >= 20f && hourlyTemperature <= 23f) ||
			(hourlyTemperature >= 29f && hourlyTemperature <= 30f)) {
			totalScore += 7;  // 적합
		} else if ((hourlyTemperature >= 16f && hourlyTemperature <= 19f) ||
			hourlyTemperature >= 31f) {
			totalScore += 4;  // 주의
		} else if (hourlyTemperature <= 15f) {
			totalScore += 0;  // 부적합
		}

		// 풍속
		float windSpeed = weather.getWindSpeed();
		if (windSpeed >= 0f && windSpeed < 2f) {
			totalScore += 20; // 매우 약함 (매우 적합)
		} else if (windSpeed >= 2f && windSpeed < 4f) {
			totalScore += 15; // 약함 (적합)
		} else if (windSpeed >= 4f && windSpeed < 7f) {
			totalScore += 10; // 적당 (보통)
		} else if (windSpeed >= 7f && windSpeed < 10f) {
			totalScore += 5;  // 강함 (주의)
		}

		// 강수량
		float hourlyPrecipitation = weather.getHourlyPrecipitation();
		if (hourlyPrecipitation == 0f || hourlyPrecipitation == -99f) {
			totalScore += 10;
		} else if (hourlyPrecipitation >= 1f && hourlyPrecipitation < 8f) {
			totalScore += 7;
		} else if (hourlyPrecipitation >= 8f && hourlyPrecipitation < 11f) {
			totalScore += 5;
		} else if (hourlyPrecipitation >= 11f && hourlyPrecipitation < 20f) {
			totalScore += 2;
		}

		// 물때
		int tidePercentage = calculateTidePercentage(tideRecords);
		if (tidePercentage >= 70) {
			totalScore += 15; // 매우 적합
		} else if (tidePercentage >= 50) {
			totalScore += 10; // 적합
		} else if (tidePercentage >= 20) {
			totalScore += 5; // 주의
		}

		// 파고
		float waveHeight = weather.getWaveHeight();
		if (waveHeight >= 0f && waveHeight < 0.3f) {
			totalScore += 20; // 매우 안정 (매우 적합)
		} else if (waveHeight >= 0.3f && waveHeight < 0.7f) {
			totalScore += 15; // 안정적 (적합)
		} else if (waveHeight >= 0.7f && waveHeight < 1.3f) {
			totalScore += 10; // 주의 (보통)
		} else if (waveHeight >= 1.3f && waveHeight < 2f) {
			totalScore += 5;  // 위험 (주의)
		}

		// 수온
		float waterTemperature = weather.getWaterTemperature();
		if (waterTemperature >= 26f && waterTemperature <= 30f) {
			totalScore += 10;
		} else if (waterTemperature >= 24f && waterTemperature < 26f) {
			totalScore += 8;
		} else if (waterTemperature >= 20f && waterTemperature < 24f) {
			totalScore += 5;
		}

		return totalScore;
	}

	private int calculateSurfingScore(Weather weather, List<TideRecord> tideRecords) {
		int totalScore = 0;

		SkyCondition skyCondition = weather.getSkyCondition();
		if (weather.getPrecipitationType() == PrecipitationType.RAIN
			|| weather.getPrecipitationType() == PrecipitationType.SHOWER
			|| weather.getPrecipitationType() == PrecipitationType.RAIN_DROPS
			|| weather.getPrecipitationType() == PrecipitationType.RAIN_SNOW_DROPS) {
			totalScore += 10;
		} else if (weather.getPrecipitationType() == PrecipitationType.SNOW
			|| weather.getPrecipitationType() == PrecipitationType.SNOW_FLURRY) {
			totalScore += 5;
		} else if (skyCondition == SkyCondition.SUNNY) {
			totalScore += 20;
		} else if (skyCondition == SkyCondition.CLOUDS || skyCondition == SkyCondition.CLOUDY) {
			totalScore += 15;
		}

		// 기온
		float hourlyTemperature = weather.getHourlyTemperature();
		if (hourlyTemperature >= 24f && hourlyTemperature <= 28f) {
			totalScore += 10; // 매우 적합
		} else if ((hourlyTemperature >= 20f && hourlyTemperature <= 23f) ||
			(hourlyTemperature >= 29f && hourlyTemperature <= 30f)) {
			totalScore += 7;  // 적합
		} else if ((hourlyTemperature >= 16f && hourlyTemperature <= 19f) ||
			hourlyTemperature >= 31f) {
			totalScore += 4;  // 주의
		} else if (hourlyTemperature <= 15f) {
			totalScore += 0;  // 부적합
		}

		// 풍속
		float windSpeed = weather.getWindSpeed();
		if (windSpeed >= 0f && windSpeed < 2f) {
			totalScore += 20; // 매우 약함 (매우 적합)
		} else if (windSpeed >= 2f && windSpeed < 4f) {
			totalScore += 15; // 약함 (적합)
		} else if (windSpeed >= 4f && windSpeed < 7f) {
			totalScore += 10; // 적당 (보통)
		} else if (windSpeed >= 7f && windSpeed < 10f) {
			totalScore += 5;  // 강함 (주의)
		}

		// 강수량
		float hourlyPrecipitation = weather.getHourlyPrecipitation();
		if (hourlyPrecipitation == 0f || hourlyPrecipitation == -99f) {
			totalScore += 10;
		} else if (hourlyPrecipitation >= 1f && hourlyPrecipitation < 8f) {
			totalScore += 7;
		} else if (hourlyPrecipitation >= 8f && hourlyPrecipitation < 11f) {
			totalScore += 5;
		} else if (hourlyPrecipitation >= 11f && hourlyPrecipitation < 20f) {
			totalScore += 2;
		}

		// 물때
		int tidePercentage = calculateTidePercentage(tideRecords);
		if (tidePercentage >= 70) {
			totalScore += 15; // 매우 적합
		} else if (tidePercentage >= 50) {
			totalScore += 10; // 적합
		} else if (tidePercentage >= 20) {
			totalScore += 5; // 주의
		}

		// 파고
		float waveHeight = weather.getWaveHeight();
		if (waveHeight >= 0f && waveHeight < 0.3f) {
			totalScore += 20; // 매우 안정 (매우 적합)
		} else if (waveHeight >= 0.3f && waveHeight < 0.7f) {
			totalScore += 15; // 안정적 (적합)
		} else if (waveHeight >= 0.7f && waveHeight < 1.3f) {
			totalScore += 10; // 주의 (보통)
		} else if (waveHeight >= 1.3f && waveHeight < 2f) {
			totalScore += 5;  // 위험 (주의)
		}

		// 수온
		float waterTemperature = weather.getWaterTemperature();
		if (waterTemperature >= 26f && waterTemperature <= 30f) {
			totalScore += 10;
		} else if (waterTemperature >= 24f && waterTemperature < 26f) {
			totalScore += 8;
		} else if (waterTemperature >= 20f && waterTemperature < 24f) {
			totalScore += 5;
		}

		return totalScore;
	}

	private int calculateSwimmingScore(Weather weather, List<TideRecord> tideRecords) {
		int totalScore = 0;

		SkyCondition skyCondition = weather.getSkyCondition();
		if (weather.getPrecipitationType() == PrecipitationType.RAIN
			|| weather.getPrecipitationType() == PrecipitationType.SHOWER
			|| weather.getPrecipitationType() == PrecipitationType.RAIN_DROPS
			|| weather.getPrecipitationType() == PrecipitationType.RAIN_SNOW_DROPS) {
			totalScore += 10;
		} else if (weather.getPrecipitationType() == PrecipitationType.SNOW
			|| weather.getPrecipitationType() == PrecipitationType.SNOW_FLURRY) {
			totalScore += 5;
		} else if (skyCondition == SkyCondition.SUNNY) {
			totalScore += 20;
		} else if (skyCondition == SkyCondition.CLOUDS || skyCondition == SkyCondition.CLOUDY) {
			totalScore += 15;
		}

		float hourlyTemperature = weather.getHourlyTemperature();
		if (hourlyTemperature >= 24f && hourlyTemperature <= 28f) {
			totalScore += 10; // 매우 적합
		} else if ((hourlyTemperature >= 20f && hourlyTemperature <= 23f) ||
			(hourlyTemperature >= 29f && hourlyTemperature <= 30f)) {
			totalScore += 7;  // 적합
		} else if ((hourlyTemperature >= 16f && hourlyTemperature <= 19f) ||
			hourlyTemperature >= 31f) {
			totalScore += 4;  // 주의
		} else if (hourlyTemperature <= 15f) {
			totalScore += 0;  // 부적합
		}

		float windSpeed = weather.getWindSpeed();
		if (windSpeed >= 0f && windSpeed < 2f) {
			totalScore += 20; // 매우 약함 (매우 적합)
		} else if (windSpeed >= 2f && windSpeed < 4f) {
			totalScore += 15; // 약함 (적합)
		} else if (windSpeed >= 4f && windSpeed < 7f) {
			totalScore += 10; // 적당 (보통)
		} else if (windSpeed >= 7f && windSpeed < 10f) {
			totalScore += 5;  // 강함 (주의)
		}

		float hourlyPrecipitation = weather.getHourlyPrecipitation();
		if (hourlyPrecipitation == 0f || hourlyPrecipitation == -99f) {
			totalScore += 10;
		} else if (hourlyPrecipitation >= 1f && hourlyPrecipitation < 8f) {
			totalScore += 7;
		} else if (hourlyPrecipitation >= 8f && hourlyPrecipitation < 11f) {
			totalScore += 5;
		} else if (hourlyPrecipitation >= 11f && hourlyPrecipitation < 20f) {
			totalScore += 2;
		}

		int tidePercentage = calculateTidePercentage(tideRecords);
		if (tidePercentage >= 50 && tidePercentage < 70) {
			totalScore += 15; // 매우 적합
		} else if (tidePercentage >= 40 && tidePercentage < 50) {
			totalScore += 10; // 적합
		} else if (tidePercentage >= 10 && tidePercentage < 40) {
			totalScore += 5; // 주의
		}

		// 파고
		float waveHeight = weather.getWaveHeight();
		if (waveHeight >= 0f && waveHeight < 0.3f) {
			totalScore += 20; // 매우 안정 (매우 적합)
		} else if (waveHeight >= 0.3f && waveHeight < 0.7f) {
			totalScore += 15; // 안정적 (적합)
		} else if (waveHeight >= 0.7f && waveHeight < 1.3f) {
			totalScore += 10; // 주의 (보통)
		} else if (waveHeight >= 1.3f && waveHeight < 2f) {
			totalScore += 5;  // 위험 (주의)
		}

		float waterTemperature = weather.getWaterTemperature();
		if (waterTemperature >= 26f && waterTemperature <= 30f) {
			totalScore += 10;
		} else if (waterTemperature >= 24f && waterTemperature < 26f) {
			totalScore += 8;
		} else if (waterTemperature >= 20f && waterTemperature < 24f) {
			totalScore += 5;
		}

		return totalScore;
	}

	private int calculateKayakingPaddleBoardingScore(Weather weather, List<TideRecord> tideRecords) {
		int totalScore = 0;

		SkyCondition skyCondition = weather.getSkyCondition();
		if (weather.getPrecipitationType() == PrecipitationType.RAIN
			|| weather.getPrecipitationType() == PrecipitationType.SHOWER
			|| weather.getPrecipitationType() == PrecipitationType.RAIN_DROPS
			|| weather.getPrecipitationType() == PrecipitationType.RAIN_SNOW_DROPS) {
			totalScore += 10;
		} else if (weather.getPrecipitationType() == PrecipitationType.SNOW
			|| weather.getPrecipitationType() == PrecipitationType.SNOW_FLURRY) {
			totalScore += 5;
		} else if (skyCondition == SkyCondition.SUNNY) {
			totalScore += 20;
		} else if (skyCondition == SkyCondition.CLOUDS || skyCondition == SkyCondition.CLOUDY) {
			totalScore += 15;
		}

		float hourlyTemperature = weather.getHourlyTemperature();
		if (hourlyTemperature >= 24f && hourlyTemperature <= 28f) {
			totalScore += 10; // 매우 적합
		} else if ((hourlyTemperature >= 20f && hourlyTemperature <= 23f) ||
			(hourlyTemperature >= 29f && hourlyTemperature <= 30f)) {
			totalScore += 7;  // 적합
		} else if ((hourlyTemperature >= 16f && hourlyTemperature <= 19f) ||
			hourlyTemperature >= 31f) {
			totalScore += 4;  // 주의
		} else if (hourlyTemperature <= 15f) {
			totalScore += 0;  // 부적합
		}

		float windSpeed = weather.getWindSpeed();
		if (windSpeed >= 0f && windSpeed < 2f) {
			totalScore += 20; // 매우 약함 (매우 적합)
		} else if (windSpeed >= 2f && windSpeed < 4f) {
			totalScore += 15; // 약함 (적합)
		} else if (windSpeed >= 4f && windSpeed < 7f) {
			totalScore += 10; // 적당 (보통)
		} else if (windSpeed >= 7f && windSpeed < 10f) {
			totalScore += 5;  // 강함 (주의)
		}

		float hourlyPrecipitation = weather.getHourlyPrecipitation();
		if (hourlyPrecipitation == 0f || hourlyPrecipitation == -99f) {
			totalScore += 10;
		} else if (hourlyPrecipitation >= 1f && hourlyPrecipitation < 8f) {
			totalScore += 7;
		} else if (hourlyPrecipitation >= 8f && hourlyPrecipitation < 11f) {
			totalScore += 5;
		} else if (hourlyPrecipitation >= 11f && hourlyPrecipitation < 20f) {
			totalScore += 2;
		}

		int tidePercentage = calculateTidePercentage(tideRecords);
		if (tidePercentage >= 50 && tidePercentage < 70) {
			totalScore += 15; // 매우 적합
		} else if (tidePercentage >= 40 && tidePercentage < 50) {
			totalScore += 10; // 적합
		} else if (tidePercentage >= 10 && tidePercentage < 40) {
			totalScore += 5; // 주의
		}

		float waveHeight = weather.getWaveHeight();
		if (waveHeight >= 0f && waveHeight < 0.3f) {
			totalScore += 20; // 매우 안정 (매우 적합)
		} else if (waveHeight >= 0.3f && waveHeight < 0.7f) {
			totalScore += 15; // 안정적 (적합)
		} else if (waveHeight >= 0.7f && waveHeight < 1.3f) {
			totalScore += 10; // 주의 (보통)
		} else if (waveHeight >= 1.3f && waveHeight < 2f) {
			totalScore += 5;  // 위험 (주의)
		}

		float waterTemperature = weather.getWaterTemperature();
		if (waterTemperature >= 26f && waterTemperature <= 30f) {
			totalScore += 10;
		} else if (waterTemperature >= 24f && waterTemperature < 26f) {
			totalScore += 8;
		} else if (waterTemperature >= 20f && waterTemperature < 24f) {
			totalScore += 5;
		}

		return totalScore;
	}

	private int calculateTidePercentage(List<TideRecord> tideRecords) {
		tideRecords.sort(Comparator.comparing(TideRecord::getTidalTime));
		LocalDateTime now = LocalDateTime.now();

		for (int i = 0; i < tideRecords.size() - 1; i++) {
			LocalDateTime start = tideRecords.get(i).getTidalTime();
			LocalDateTime end = tideRecords.get(i + 1).getTidalTime();

			if (now.isAfter(start) && now.isBefore(end)) {
				long totalMinutes = ChronoUnit.MINUTES.between(start, end);
				long elapsedMinutes = ChronoUnit.MINUTES.between(start, now);
				return (int)((double)elapsedMinutes / totalMinutes * 100);
			}
		}
		return 0; // 범위 밖의 시간인 경우
	}

	public List<AvailableTime> getAvailableTime(Integer date, Integer hour) {
		return weatherRepository.getWeatherIsUpdated(date, hour * 100).stream()
			.collect(Collectors.groupingBy(
				Weather::getDate,
				Collectors.mapping(weather -> weather.getTime() / 100, Collectors.toSet()) // Remove duplicates
			))
			.entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.map(entry -> new AvailableTime(
				entry.getKey(),
				entry.getValue().stream().sorted().toList()
			))
			.toList();
	}

	private List<Integer> calculateRelevantDates() {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		return List.of(
			Integer.parseInt(now.minusDays(1).format(formatter)), // 어제
			Integer.parseInt(now.format(formatter)),             // 오늘
			Integer.parseInt(now.plusDays(1).format(formatter))  // 내일
		);
	}
}

