package nambang_swag.bada_on.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nambang_swag.bada_on.request.AvailableTimeRequest;
import nambang_swag.bada_on.response.AvailableTime;
import nambang_swag.bada_on.response.WarningResponse;
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
	public ResponseEntity<WeatherSummary> getWeatherSummary(
		@RequestParam("id") Long id,
		@RequestParam("date") int date,
		@RequestParam("hour") int hour
	) {
		return ResponseEntity.ok().body(weatherService.getWeatherSummary(id, date, hour));
	}

	@Operation(summary = "등록장소의 세부 날씨 조회", description = "등록 장소의 세부 날씨 조회")
	@GetMapping("/details")
	public ResponseEntity<WeatherDetail> getWeatherDetails(
		@RequestParam("id") Long id,
		@RequestParam("date") int date,
		@RequestParam("hour") int hour
	) {
		return ResponseEntity.ok().body(weatherService.getWeatherDetail(id, date, hour));
	}

	@Operation(summary = "조회 가능한 시간 조회", description = "날씨 조회가 가능한 시간 조회")
	@GetMapping("/available")
	public ResponseEntity<List<AvailableTime>> getAvailableTime(
		@Valid AvailableTimeRequest request
	) {
		return ResponseEntity.ok(weatherService.getAvailableTime(Integer.valueOf(request.date())));
	}

	@Operation(summary = "발생한 기상특보 조회", description = "현재 발행된 기상특보가 발령된 장소와 기상특보를 모아 보여줍니다.")
	@GetMapping("/warning")
	public ResponseEntity<List<WarningResponse>> getWarnings() {
		return ResponseEntity.ok(weatherService.getAllWeatherWarning());
	}
}
