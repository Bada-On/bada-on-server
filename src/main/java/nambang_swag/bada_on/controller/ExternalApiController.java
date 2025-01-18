package nambang_swag.bada_on.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import nambang_swag.bada_on.service.ExternalApiService;

@Hidden
@RequiredArgsConstructor
@RequestMapping("/api/v1/fetch")
@RestController
public class ExternalApiController {

	private final ExternalApiService externalApiService;

	@GetMapping("/forecast/ultra")
	public void fetchUltraShortTermForecast() {
		externalApiService.getUltraShortTermForecast();
	}

	@GetMapping("/forecast/short")
	public void fetchShortTermForecast() {
		externalApiService.getShortTermForecast();
	}

	@GetMapping("/nowcast")
	public void fetchNowcast() {
		externalApiService.getUltraShortTermNowCast();
	}

	@GetMapping("/forecast/tide")
	public void fetchTideForecast() {
		externalApiService.getTidalForecast();
	}

	@GetMapping("/forecast/water")
	public void fetchWaterForecast() {
		externalApiService.getOceanTemperature();
	}

	@GetMapping("/forecast/daily")
	public void fetchDailyForecast() {
		externalApiService.getSunRiseSetData();
	}

	@GetMapping("/warning")
	public void fetchWarningData() {
		externalApiService.getWeatherWarning();
	}
}
