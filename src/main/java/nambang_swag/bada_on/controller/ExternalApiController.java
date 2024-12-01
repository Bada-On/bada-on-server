package nambang_swag.bada_on.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nambang_swag.bada_on.service.ExternalApiService;

@RequiredArgsConstructor
@RequestMapping("/fetch")
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
}
