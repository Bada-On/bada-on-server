package nambang_swag.bada_on.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nambang_swag.bada_on.request.PlaceRegister;
import nambang_swag.bada_on.response.PlaceInfo;
import nambang_swag.bada_on.response.PlaceList;
import nambang_swag.bada_on.service.PlaceService;

@RequiredArgsConstructor
@RequestMapping("/api/places")
@RestController
public class PlaceController {

	private final PlaceService placeService;

	@Operation(summary = "장소 등록", description = "장소를 등록합니다.")
	@PostMapping
	public ResponseEntity<PlaceInfo> register(@RequestBody @Valid PlaceRegister placeRegister) {
		return ResponseEntity.ok().body(placeService.register(placeRegister));
	}

	@Operation(summary = "모든 등록 장소 조회", description = "모든 등록 장소와 장소에서 가능한 활동들을 조회합니다.")
	@GetMapping
	public ResponseEntity<PlaceList> getList() {
		return ResponseEntity.ok().body(placeService.getListOfPlaces());
	}

	@Operation(summary = "활동을 통한 등록 장소 조회", description = "활동으로 필터링된 등록 장소들을 조회합니다.")
	@GetMapping("/find")
	public ResponseEntity<PlaceList> getPlaceByActivity(@RequestParam String activity) {
		return ResponseEntity.ok().body(placeService.findPlaceByActivity(activity));
	}
}
