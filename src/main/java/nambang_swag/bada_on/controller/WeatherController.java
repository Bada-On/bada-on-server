package nambang_swag.bada_on.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import nambang_swag.bada_on.response.AvailableTime;
import nambang_swag.bada_on.response.WeatherDetail;
import nambang_swag.bada_on.response.WeatherSummary;
import nambang_swag.bada_on.service.WeatherService;

@Tag(name = "날씨 정보 API")
@RequiredArgsConstructor
@RequestMapping("/api/v1/weather")
@RestController
public class WeatherController {

	private final WeatherService weatherService;

	@Operation(summary = "등록장소에서의 활동 추천도 조회", description = "입력된 위치의 활동 추천도 리스트를 조회")
	@GetMapping("/summary")
	public ResponseEntity<List<WeatherSummary>> getWeatherSummary(
		@Parameter(description = "등록 장소 ID")
		@RequestParam("id")
		Long id,
		@Parameter(description = "activity(스노클링 - snorkeling, 다이빙 - diving, 해수욕 - swimming, 서핑 - surfing, 카약/패들보드 - kayakingPaddleBoarding)")
		@RequestParam("category") String category) {
		return ResponseEntity.ok().body(weatherService.getWeatherSummary(id, category));
	}

	@Operation(summary = "등록장소의 세부 날씨 조회", description = "등록 장소의 세부 날씨 조회")
	@GetMapping("/details")
	public ResponseEntity<List<WeatherDetail>> getWeatherDetails(
		@Parameter(description = "등록 장소 ID")
		@RequestParam("id")
		Long id) {
		return ResponseEntity.ok().body(weatherService.getWeatherDetail(id));
	}

	@Operation(summary = "조회 가능한 시간 조회", description = "날씨 조회가 가능한 시간 조회")
	@GetMapping("/available")
	public ResponseEntity<List<AvailableTime>> getAvailableTime(
		@RequestParam("date") Integer date,
		@RequestParam("hour") Integer hour
	) {
		return ResponseEntity.ok(weatherService.getAvailableTime(date, hour));
	}
}
