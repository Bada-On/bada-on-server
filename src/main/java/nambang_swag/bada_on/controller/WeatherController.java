package nambang_swag.bada_on.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nambang_swag.bada_on.response.WeatherDetail;
import nambang_swag.bada_on.response.WeatherSummary;
import nambang_swag.bada_on.service.ExternalApiService;
import nambang_swag.bada_on.service.WeatherService;

@RequiredArgsConstructor
@RequestMapping("/api/weather")
@RestController
public class WeatherController {

	private final WeatherService weatherService;
	private final ExternalApiService externalApiService;

	@GetMapping("/summary/{id}")
	public ResponseEntity<List<WeatherSummary>> getWeatherSummary(
		@PathVariable("id") Long id,
		@RequestParam("category") String category) {
		return ResponseEntity.ok().body(weatherService.getWeatherSummary(id, category));
	}

	@GetMapping("/details/{id}")
	public ResponseEntity<WeatherDetail> getWeatherDetails(@PathVariable("id") Long id) {
		return ResponseEntity.ok().body(weatherService.getWeatherDetail(id));
	}
}
