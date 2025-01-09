package nambang_swag.bada_on.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nambang_swag.bada_on.entity.DailyWeather;
import nambang_swag.bada_on.entity.Place;
import nambang_swag.bada_on.entity.TideRecord;
import nambang_swag.bada_on.entity.Weather;
import nambang_swag.bada_on.external.SunRiseSetForeCastApiResponse;
import nambang_swag.bada_on.external.TideApiResponse;
import nambang_swag.bada_on.external.WaterTemperatureForecastApiResponse;
import nambang_swag.bada_on.external.WeatherForeCastApiResponse;
import nambang_swag.bada_on.external.WeatherNowCastApiResponse;
import nambang_swag.bada_on.repository.DailyWeatherRepository;
import nambang_swag.bada_on.repository.PlaceRepository;
import nambang_swag.bada_on.repository.TideRepository;
import nambang_swag.bada_on.repository.WeatherRepository;
import nambang_swag.bada_on.constant.TideObservatory;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ExternalApiService {

	private final PlaceRepository placeRepository;
	private final WeatherRepository weatherRepository;

	private final ObjectMapper objectMapper;
	private final TideRepository tideRepository;

	private final DailyWeatherRepository dailyWeatherRepository;

	@Value("${secrets.external.WEATHER_API_BASE_URL}")
	private String WEATHER_API_BASE_URL;

	@Value("${secrets.external.WEATHER_API_KEY}")
	private String WEATHER_API_KEY;

	@Value("${secrets.external.OCEAN_API_BASE_URL}")
	private String OCEAN_API_BASE_URL;

	@Value("${secrets.external.OCEAN_API_KEY}")
	private String OCEAN_API_KEY;

	@Value("${secrets.external.PUBLIC_API_BASE_URL}")
	private String PUBLIC_API_BASE_URL;

	@Value("${secrets.external.PUBLIC_API_KEY}")
	private String PUBLIC_API_KEY;

	// 초단기 예보 가져오기 - 매 시간 30분에 발표, 45분에 api반영
	@Transactional
	public void getUltraShortTermForecast() {
		log.info("초단기 예보 정보 수집 시작");
		List<Place> places = placeRepository.findAll();
		for (Place place : places) {
			WeatherForeCastApiResponse response = callUltraShortTermForecastApi(place);
			processWeatherForecastData(response, place);
		}
		log.info("초단기 예보 정보 수집 종료");
	}

	// 단기 예보 가져오기 - 매 시간 10분에 api반영
	@Transactional
	public void getShortTermForecast() {
		log.info("단기 예보 정보 수집 시작");
		List<Place> places = placeRepository.findAll();
		for (Place place : places) {
			WeatherForeCastApiResponse response = callShortTermForecastApi(place);
			processWeatherForecastData(response, place);
		}
		log.info("단기 예보 정보 수집 종료");
	}

	// 초단기 실황 가져오기
	@Transactional
	public void getUltraShortTermNowCast() {
		log.info("초단기 실황 정보 수집 시작");
		List<Place> places = placeRepository.findAll();
		for (Place place : places) {
			WeatherNowCastApiResponse response = callUltraShortTermNowCastApi(place);
			processWeatherNowCastData(response, place);
		}
		log.info("초단기 실황 정보 수집 종료");
	}

	// 조석예보 가져오기
	@Transactional
	public void getTidalForecast() {
		log.info("조석예보 정보 수집 시작");
		LocalDateTime now = LocalDateTime.now();
		for (int i = 0; i < 3; i++) {
			LocalDateTime time = now.plusDays(i);
			for (TideObservatory tideObservatory : TideObservatory.values()) {
				try {
					TideApiResponse tideApiResponse = callTidalForecastApi(tideObservatory, time);
					processTidalForecastData(tideApiResponse, tideObservatory);
				} catch (Exception e) {
					log.error(e.getMessage());
				}
			}
		}
		log.info("조석예보 정보 수집 종료");
	}

	// 일출일몰 예보 가져오기
	@Transactional
	public void getSunRiseSetData() {
		log.info("일출 일몰 정보 수집 시작");
		List<Place> places = placeRepository.findAll();
		LocalDateTime now = LocalDateTime.now();
		String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		for (Place place : places) {
			SunRiseSetForeCastApiResponse response = callSunRiseSetForecastApi(place, baseDate);
			processSunRiseSetData(response, place, baseDate);
		}
		log.info("일출 일몰 정보 수집 종료");
	}

	// 수온 정보 가져오기
	@Transactional
	public void getOceanTemperature() {
		log.info("수온 정보 수집 시작");
		List<Place> places = placeRepository.findAll();
		for (Place place : places) {
			WaterTemperatureForecastApiResponse response = callWaterTemperatureForecastApi(place);
			processWaterTemperatureData(response, place);
		}
		log.info("수온 정보 수집 종료");
	}

	@Scheduled(cron = "0 11 * * * *")
	public void fetchShortTermForecastData() {
		getShortTermForecast();
	}

	@Scheduled(cron = "0 46 * * * *")
	public void fetchUltraShortTermForecastData() {
		getUltraShortTermForecast();
	}

	@Scheduled(cron = "0 0 0 * * *")
	public void scheduledTask() {
		getOceanTemperature();
		getTidalForecast();
	}

	@Scheduled(cron = "0 15 * * * *")
	public void fetchUltraShortTermNowCastData() {
		getUltraShortTermNowCast();
	}

	private WeatherForeCastApiResponse callUltraShortTermForecastApi(Place place) {
		LocalDateTime now = LocalDateTime.now();
		String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String baseTime = now.format(DateTimeFormatter.ofPattern("HH30"));

		RestClient restClient = RestClient.builder()
			.baseUrl(WEATHER_API_BASE_URL)
			.build();

		return restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/getUltraSrtFcst")
				.queryParam("serviceKey", WEATHER_API_KEY)
				.queryParam("pageNo", 1)
				.queryParam("numOfRows", 1000)
				.queryParam("dataType", "JSON")
				.queryParam("base_date", baseDate)
				.queryParam("base_time", baseTime)
				.queryParam("nx", place.getNx())
				.queryParam("ny", place.getNy())
				.build())
			.retrieve()
			.body(WeatherForeCastApiResponse.class);
	}

	private WeatherForeCastApiResponse callShortTermForecastApi(Place place) {
		LocalDateTime closestBaseTime = findClosestBaseTime();

		String baseDate = closestBaseTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String baseTime = closestBaseTime.format(DateTimeFormatter.ofPattern("HH00"));
		RestClient restClient = RestClient.builder()
			.baseUrl(WEATHER_API_BASE_URL)
			.build();

		return restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/getVilageFcst")
				.queryParam("serviceKey", WEATHER_API_KEY)
				.queryParam("pageNo", 1)
				.queryParam("numOfRows", 1000)
				.queryParam("dataType", "JSON")
				.queryParam("base_date", baseDate)
				.queryParam("base_time", baseTime)
				.queryParam("nx", place.getNx())
				.queryParam("ny", place.getNy())
				.build())
			.retrieve()
			.body(WeatherForeCastApiResponse.class);
	}

	private WeatherNowCastApiResponse callUltraShortTermNowCastApi(Place place) {
		LocalDateTime now = LocalDateTime.now();
		String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String baseTime = now.format(DateTimeFormatter.ofPattern("HH00"));

		RestClient restClient = RestClient.builder()
			.baseUrl(WEATHER_API_BASE_URL)
			.build();

		return restClient.get()
			.uri(uriBuilder -> uriBuilder.path("/getUltraSrtNcst")
				.queryParam("serviceKey", WEATHER_API_KEY)
				.queryParam("pageNo", 1)
				.queryParam("numOfRows", 1000)
				.queryParam("dataType", "JSON")
				.queryParam("base_date", baseDate)
				.queryParam("base_time", baseTime)
				.queryParam("nx", place.getNx())
				.queryParam("ny", place.getNy())
				.build())
			.retrieve()
			.body(WeatherNowCastApiResponse.class);
	}

	private TideApiResponse callTidalForecastApi(TideObservatory tideObservatory, LocalDateTime now) {
		String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		RestClient restClient = RestClient.builder()
			.baseUrl(OCEAN_API_BASE_URL)
			.build();

		try {
			String responseBody = restClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/tideObsPreTab/search.do")
					.queryParam("ServiceKey", OCEAN_API_KEY)
					.queryParam("ObsCode", tideObservatory.getCode())
					.queryParam("Date", baseDate)
					.queryParam("ResultType", "json")
					.build())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.body(String.class);
			return objectMapper.readValue(responseBody, TideApiResponse.class);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private SunRiseSetForeCastApiResponse callSunRiseSetForecastApi(Place place, String baseDate) {
		RestClient restClient = RestClient.builder()
			.baseUrl(PUBLIC_API_BASE_URL)
			.build();
		XmlMapper xmlMapper = new XmlMapper();
		try {
			String responseBody = restClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/getLCRiseSetInfo")
					.queryParam("serviceKey", PUBLIC_API_KEY)
					.queryParam("locdate", baseDate)
					.queryParam("longitude", place.getLongitude())
					.queryParam("latitude", place.getLatitude())
					.queryParam("dnYn", "Y")
					.build())
				.retrieve()
				.body(String.class);
			return xmlMapper.readValue(responseBody,
				SunRiseSetForeCastApiResponse.class);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private WaterTemperatureForecastApiResponse callWaterTemperatureForecastApi(Place place) {
		RestClient restClient = RestClient.builder()
			.baseUrl(OCEAN_API_BASE_URL)
			.build();

		try {
			String responseBody = restClient.get()
				.uri(uriBuilder -> uriBuilder
					.path("/romsTemp/search.do")
					.queryParam("ServiceKey", OCEAN_API_KEY)
					.queryParam("ObsLon", place.getLongitude())
					.queryParam("ObsLat", place.getLatitude())
					.queryParam("ResultType", "json")
					.build())
				.retrieve()
				.body(String.class);
			return objectMapper.readValue(responseBody, WaterTemperatureForecastApiResponse.class);
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	private void processWeatherForecastData(WeatherForeCastApiResponse response, Place place) {
		if (!response.getResponse().getHeader().getResultCode().equals("00")) {
			log.info("API Call failed: {}", response.getResponse().getHeader().getResultMsg());
			return;
		}

		List<WeatherForeCastApiResponse.Item> itemList = response.getResponse().getBody().getItems().getItem();
		for (WeatherForeCastApiResponse.Item item : itemList) {
			int forecastDate = item.getFcstDate();
			int forecastTime = item.getFcstTime();
			Weather weather = weatherRepository.findByDateAndTimeAndPlace(forecastDate, forecastTime, place)
				.orElseGet(() -> new Weather(place, forecastDate, forecastTime));
			weather.updateWeatherData(item.getCategory(), item.getFcstValue());
			weatherRepository.save(weather);
		}
	}

	private void processWeatherNowCastData(WeatherNowCastApiResponse response, Place place) {
		if (!response.getResponse().getHeader().getResultCode().equals("00")) {
			log.info("API Call failed: {}", response.getResponse().getHeader().getResultMsg());
			return;
		}
		List<WeatherNowCastApiResponse.Item> items = response.getResponse().getBody().getItems().getItem();
		for (WeatherNowCastApiResponse.Item item : items) {
			int forecastDate = item.getBaseDate();
			int forecastTime = item.getBaseTime();
			Weather weather = weatherRepository.findByDateAndTimeAndPlace(forecastDate, forecastTime, place)
				.orElseGet(() -> new Weather(place, forecastDate, forecastTime));
			weather.updateWeatherData(item.getCategory(), item.getObsrValue());
			weatherRepository.save(weather);
		}
	}

	private void processTidalForecastData(TideApiResponse response, TideObservatory observatory) {
		List<TideApiResponse.TideData> data = response.getResult().getData();
		for (TideApiResponse.TideData tideData : data) {
			LocalDateTime tphTime = LocalDateTime.parse(tideData.getTphTime(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

			TideRecord tideRecord = tideRepository.findByDateAndTideObservatoryAndTidalTime(
					Integer.parseInt(tphTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"))), observatory, tphTime)
				.orElseGet(() -> TideRecord.builder()
					.tideObservatory(observatory)
					.date(Integer.parseInt(tphTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"))))
					.build());

			if (tideRecord.getTidalTime() != tphTime) {
				tideRecord.updateForecastData(observatory, tideData.getTphLevel(), tphTime, tideData.getHlCode());
				tideRepository.save(tideRecord);
			}
		}
	}

	private void processSunRiseSetData(SunRiseSetForeCastApiResponse response, Place place, String baseDate) {
		DailyWeather dailyWeather = dailyWeatherRepository.findByDateAndPlace(Integer.parseInt(baseDate), place)
			.orElseGet(() -> DailyWeather.builder()
				.date(Integer.parseInt(baseDate))
				.place(place)
				.build());
		int civile = Integer.parseInt(response.getBody().getItems().getItem().getFirst().getCivile().trim());
		int civilm = Integer.parseInt(response.getBody().getItems().getItem().getFirst().getCivilm().trim());
		dailyWeather.updateSunRiseSetInfo(civile, civilm);
		dailyWeatherRepository.save(dailyWeather);
	}

	private void processWaterTemperatureData(WaterTemperatureForecastApiResponse response, Place place) {
		List<WaterTemperatureForecastApiResponse.WaterTemperatureData> dataList = response.getResult().getData();
		for (WaterTemperatureForecastApiResponse.WaterTemperatureData data : dataList) {
			Weather weather = weatherRepository.findByDateAndTimeAndPlace(data.getDate(), data.getHour() * 100, place)
				.orElseGet(() -> new Weather(place, data.getDate(), data.getHour() * 100));
			weather.updateWaterTemperature(data.getTemperature());
			weatherRepository.save(weather);
		}
	}

	// 가장 가까운 단기예보 BaseTime가져오기
	public static LocalDateTime findClosestBaseTime() {
		LocalDateTime current = LocalDateTime.now();
		int[] baseTimes = {2, 5, 8, 11, 14, 17, 20, 23};

		int currentHour = current.getHour();
		LocalDateTime closest = null;

		for (int baseTime : baseTimes) {
			if (baseTime <= currentHour) {
				closest = current.withHour(baseTime).withMinute(0);
			}
		}

		if (closest == null) {
			closest = current.minusDays(1).withHour(23).withMinute(0);
		}
		return closest;
	}
}
