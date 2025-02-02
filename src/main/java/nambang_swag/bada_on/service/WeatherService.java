package nambang_swag.bada_on.service;

import static nambang_swag.bada_on.constant.Activity.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
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
import nambang_swag.bada_on.entity.Warning;
import nambang_swag.bada_on.entity.WarningStatus;
import nambang_swag.bada_on.entity.Weather;
import nambang_swag.bada_on.exception.PlaceNotFound;
import nambang_swag.bada_on.exception.WeatherNotFound;
import nambang_swag.bada_on.repository.PlaceRepository;
import nambang_swag.bada_on.repository.TideRepository;
import nambang_swag.bada_on.repository.WarningRepository;
import nambang_swag.bada_on.repository.WeatherRepository;
import nambang_swag.bada_on.response.ActivityScore;
import nambang_swag.bada_on.response.AvailableTime;
import nambang_swag.bada_on.response.TideInfo;
import nambang_swag.bada_on.response.WarningDetail;
import nambang_swag.bada_on.response.WarningResponse;
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
	private final WarningRepository warningRepository;

	public WeatherSummary getWeatherSummary(Long id, int date, int hour) {
		Place place = placeRepository.findById(id).orElseThrow(PlaceNotFound::new);
		Weather weather = weatherRepository.findByDateAndTimeAndPlace(date, hour * 100, place)
			.orElseThrow(WeatherNotFound::new);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime requestTime = toLocalDateTime(date, hour);
		int first = Integer.parseInt(requestTime.minusDays(1).format(formatter));
		int last = Integer.parseInt(requestTime.plusDays(1).format(formatter));
		TideObservatory tideObservatory = TideObservatory.findNearest(place.getLatitude(), place.getLongitude());
		List<TideRecord> tideRecords = tideRepository.findAllByDatesAndTideObservatory(first, last, tideObservatory);

		List<WarningStatus> statuses = List.of(WarningStatus.ISSUED, WarningStatus.MODIFIED);
		List<String> stringWarningList = warningRepository.findAllByPlaceRegionAndStatusIn(
				place.getLandRegion(), place.getSeaRegion(),
				statuses).stream().map(Warning::getWarningMessage)
			.toList();

		List<String> recommendActivities =
			stringWarningList.isEmpty() ? getRecommendActivities(weather, tideRecords, requestTime) : new ArrayList<>();

		int tidePercentage = calculateTidePercentage(tideRecords, requestTime);
		return WeatherSummary.of(
			weather,
			stringWarningList,
			recommendActivities,
			tidePercentage
		);
	}

	public WeatherDetail getWeatherDetail(Long placeId, int date, int hour) {
		Place place = placeRepository.findById(placeId).orElseThrow(PlaceNotFound::new);
		Weather weather = weatherRepository.findByDateAndTimeAndPlace(date, hour * 100, place)
			.orElseThrow(WeatherNotFound::new);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime requestTime = toLocalDateTime(date, hour);
		int first = Integer.parseInt(requestTime.minusDays(1).format(formatter));
		int last = Integer.parseInt(requestTime.plusDays(1).format(formatter));
		TideObservatory tideObservatory = TideObservatory.findNearest(place.getLatitude(), place.getLongitude());
		List<TideRecord> tideRecords = tideRepository.findAllByDatesAndTideObservatory(first, last, tideObservatory);

		TideInfo closestPreviousTideRecord = findClosestPreviousTideRecord(tideRecords, requestTime);
		TideInfo closestNextTideRecord = findClosestNextTideRecord(tideRecords, requestTime);

		List<TideInfo> tideInfoList = new ArrayList<>();
		if (closestPreviousTideRecord != null) {
			tideInfoList.add(closestPreviousTideRecord);
		}
		if (closestNextTideRecord != null) {
			tideInfoList.add(closestNextTideRecord);
		}

		List<WarningDetail> warningDetails = new ArrayList<>();
		warningDetails.addAll(warningRepository.findAllByRegionAndStatusIn(place.getLandRegion(),
				List.of(WarningStatus.ISSUED, WarningStatus.MODIFIED)).stream()
			.map(WarningDetail::from)
			.toList());
		warningDetails.addAll(warningRepository.findAllByRegionAndStatusIn(place.getSeaRegion(),
				List.of(WarningStatus.ISSUED, WarningStatus.MODIFIED)).stream()
			.map(WarningDetail::from)
			.toList());

		int tidePercentage = calculateTidePercentage(tideRecords, requestTime);
		return WeatherDetail.of(
			weather,
			warningDetails,
			tideInfoList,
			getAllScores(weather, tideRecords, requestTime),
			tidePercentage
		);
	}

	private List<String> getRecommendActivities(Weather weather, List<TideRecord> tideRecords,
		LocalDateTime requestTIme) {
		List<Activity> activities = Arrays.asList(
			SNORKELING,
			SWIMMING,
			DIVING,
			SURFING,
			PADDlING
		);

		Map<Activity, Integer> activityScores = new LinkedHashMap<>();

		for (Activity activity : activities) {
			int score = calculateActivityScore(activity, weather, tideRecords, requestTIme);
			activityScores.put(activity, score);
		}

		int maxScore = Collections.max(activityScores.values());

		List<String> recommendedActivities = activityScores.entrySet().stream()
			.filter(entry -> entry.getValue() == maxScore)
			.filter(entry -> entry.getValue() >= 40)
			.map(entry -> entry.getKey().getValue())
			.collect(Collectors.toList());

		return recommendedActivities.size() > 1
			? recommendedActivities.subList(0, 2)
			: recommendedActivities;
	}

	// Calculate Point
	private int calculateSnorkelingScore(Weather weather, List<TideRecord> tideRecords, LocalDateTime requestTime) {
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
		int tidePercentage = calculateTidePercentage(tideRecords, requestTime);
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

	private int calculateDivingScore(Weather weather, List<TideRecord> tideRecords, LocalDateTime requestTime) {
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
		int tidePercentage = calculateTidePercentage(tideRecords, requestTime);
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

	private int calculateSurfingScore(Weather weather, List<TideRecord> tideRecords, LocalDateTime requestTime) {
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
		int tidePercentage = calculateTidePercentage(tideRecords, requestTime);
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

	private int calculateSwimmingScore(Weather weather, List<TideRecord> tideRecords, LocalDateTime requestTime) {
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

		int tidePercentage = calculateTidePercentage(tideRecords, requestTime);
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

	private int calculatePaddlingScore(Weather weather, List<TideRecord> tideRecords, LocalDateTime requestTime) {
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

		int tidePercentage = calculateTidePercentage(tideRecords, requestTime);
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

	private int calculateTidePercentage(List<TideRecord> tideRecords, LocalDateTime requestTime) {
		tideRecords.sort(Comparator.comparing(TideRecord::getTidalTime));
		LocalDateTime now = LocalDateTime.now();

		for (int i = 0; i < tideRecords.size() - 1; i++) {
			LocalDateTime start = tideRecords.get(i).getTidalTime();
			LocalDateTime end = tideRecords.get(i + 1).getTidalTime();

			if (requestTime.isAfter(start) && requestTime.isBefore(end)) {
				long totalMinutes = ChronoUnit.MINUTES.between(start, end);
				long elapsedMinutes = ChronoUnit.MINUTES.between(start, requestTime);
				return (int)((double)elapsedMinutes / totalMinutes * 100);
			}
		}
		return 0; // 범위 밖의 시간인 경우
	}

	public List<AvailableTime> getAvailableTime(Integer date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDateTime requestTime = toLocalDateTime(date, 0);
		int requestDate = Integer.parseInt(requestTime.minusDays(1).format(formatter));

		return weatherRepository.getWeatherIsUpdated(requestDate, 0).stream()
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

	private List<ActivityScore> getAllScores(Weather weather, List<TideRecord> tideRecords, LocalDateTime requestTime) {
		List<ActivityScore> scores = new ArrayList<>();
		scores.add(
			new ActivityScore(SNORKELING.getValue(), calculateSnorkelingScore(weather, tideRecords, requestTime)));
		scores.add(new ActivityScore(DIVING.getValue(), calculateDivingScore(weather, tideRecords, requestTime)));
		scores.add(new ActivityScore(SURFING.getValue(), calculateSurfingScore(weather, tideRecords, requestTime)));
		scores.add(new ActivityScore(SWIMMING.getValue(), calculateSwimmingScore(weather, tideRecords, requestTime)));
		scores.add(new ActivityScore(PADDlING.getValue(), calculatePaddlingScore(weather, tideRecords, requestTime)));
		return scores;
	}

	private TideInfo findClosestPreviousTideRecord(List<TideRecord> tideRecords, LocalDateTime now) {
		return tideRecords.stream()
			.filter(record -> record.getTidalTime().isBefore(now))
			.max(Comparator.comparing(TideRecord::getTidalTime))
			.map(record -> new TideInfo(record.getTidalLevel(), record.getTidalTime(), record.getCode()))
			.orElse(null); // Optional에서 직접 null 반환
	}

	private TideInfo findClosestNextTideRecord(List<TideRecord> tideRecords, LocalDateTime now) {
		return tideRecords.stream()
			.filter(record -> record.getTidalTime().isAfter(now))
			.min(Comparator.comparing(TideRecord::getTidalTime))
			.map(record -> new TideInfo(record.getTidalLevel(), record.getTidalTime(), record.getCode()))
			.orElse(null); // Optional에서 직접 null 반환
	}

	private LocalDateTime toLocalDateTime(int date, int hour) {
		String dateTimeString = "";
		if (hour == 0) {
			dateTimeString = String.format("%08d00", date);
		} else {
			dateTimeString = String.format("%08d%02d", date, hour);
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHH");
		return LocalDateTime.parse(dateTimeString, formatter);
	}

	private int calculateActivityScore(Activity activity, Weather weather, List<TideRecord> tideRecords,
		LocalDateTime requestTime) {
		return switch (activity) {
			case SNORKELING -> calculateSnorkelingScore(weather, tideRecords, requestTime);
			case SWIMMING -> calculateSwimmingScore(weather, tideRecords, requestTime);
			case DIVING -> calculateDivingScore(weather, tideRecords, requestTime);
			case SURFING -> calculateSurfingScore(weather, tideRecords, requestTime);
			case PADDlING -> calculatePaddlingScore(weather, tideRecords, requestTime);
		};
	}

	public List<WarningResponse> getAllWeatherWarning() {
		List<WarningStatus> statuses = List.of(WarningStatus.ISSUED, WarningStatus.MODIFIED);
		return placeRepository.findAll().stream()
			.map(place -> {
				List<String> stringWarningList = warningRepository.findAllByPlaceRegionAndStatusIn(
						place.getLandRegion(), place.getSeaRegion(),
						statuses).stream()
					.map(Warning::getWarningMessage)
					.toList();
				return new WarningResponse(place.getId(), stringWarningList);
			})
			.filter(warningResponse -> !warningResponse.warning().isEmpty())
			.toList();
	}

}

