package nambang_swag.bada_on.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nambang_swag.bada_on.response.WeatherDetail;
import nambang_swag.bada_on.response.WeatherSummary;
import nambang_swag.bada_on.service.WeatherService;

@RequiredArgsConstructor
@RequestMapping("/api/weather")
@RestController
public class WeatherController {

	private final WeatherService weatherService;
	
	@Operation(summary = "등록장소에서의 활동 추천도 조회", description = "입력된 위치의 활동 추천도 리스트를 조회")
	@GetMapping("/summary/{id}")
	public ResponseEntity<List<WeatherSummary>> getWeatherSummary(
		@PathVariable("id") Long id,
		@RequestParam("category") String category) {
		return ResponseEntity.ok().body(weatherService.getWeatherSummary(id, category));
	}

	@Operation(summary = "등록장소의 세부 날씨 조회", description = "등록 장소의 세부 날씨 조회")
	@GetMapping("/details/{id}")
	public ResponseEntity<WeatherDetail> getWeatherDetails(@PathVariable("id") Long id) {
		return ResponseEntity.ok().body(weatherService.getWeatherDetail(id));
	}
}
