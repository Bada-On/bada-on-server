package nambang_swag.bada_on.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

	@PostMapping
	public ResponseEntity<PlaceInfo> register(@RequestBody @Valid PlaceRegister placeRegister) {
		return ResponseEntity.ok().body(placeService.register(placeRegister));
	}

	@GetMapping
	public ResponseEntity<PlaceList> getList() {
		return ResponseEntity.ok().body(placeService.getListOfPlaces());
	}

	@GetMapping("/loc")
	public ResponseEntity<PlaceList> getNearestPlace(@RequestParam(name = "lat") double lat,
		@RequestParam(name = "lon") double lon) {
		return ResponseEntity.ok().body(placeService.findNearestPlace(lon, lat));
	}

	@GetMapping("/find")
	public ResponseEntity<PlaceList> getPlaceByActivity(@RequestParam String activity) {
		return ResponseEntity.ok().body(placeService.findPlaceByActivity(activity));
	}
}
