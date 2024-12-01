package nambang_swag.bada_on.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nambang_swag.bada_on.entity.Place;
import nambang_swag.bada_on.repository.PlaceRepository;
import nambang_swag.bada_on.request.PlaceRegister;
import nambang_swag.bada_on.response.PlaceInfo;
import nambang_swag.bada_on.response.PlaceList;
import nambang_swag.bada_on.util.CoordinateConverter;
import nambang_swag.bada_on.constant.Activity;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PlaceService {

	private final PlaceRepository placeRepository;
	private final CoordinateConverter converter;

	@Transactional
	public PlaceInfo register(PlaceRegister request) {
		int[] grid = converter.convertToGrid(request.getLatitude(), request.getLongitude());
		Place place = Place.builder()
			.name(request.getName())
			.longitude(request.getLongitude())
			.latitude(request.getLatitude())
			.nx(grid[0])
			.ny(grid[1])
			.build();
		placeRepository.save(place);

		List<Activity> activities = request.getActivities().stream()
			.filter(Objects::nonNull)
			.peek(activity -> place.updateActivityStatus(activity, true))
			.toList();

		List<String> StringActivities = activities.stream()
			.map(Activity::getValue)
			.toList();

		return PlaceInfo.builder()
			.place(place)
			.activities(StringActivities)
			.build();
	}

	public PlaceList getListOfPlaces() {
		List<Place> places = placeRepository.findAll();

		List<PlaceInfo> placeInfos = new ArrayList<>();
		for (Place place : places) {
			PlaceInfo placeInfo = PlaceInfo.builder().place(place).build();
			placeInfos.add(placeInfo);
		}

		return PlaceList.builder()
			.total(places.size())
			.places(placeInfos)
			.build();
	}

	public PlaceList findPlaceByActivity(String activityName) {
		Activity activity = Activity.from(activityName);
		if (activity == null) {
			// 빈 배열 값 반환
			return null;
		}
		List<Place> placeByActivity = getPlaceByActivity(activity);
		List<PlaceInfo> placeInfoList = placeByActivity.stream()
			.map(place -> PlaceInfo.builder()
				.place(place)
				.build())
			.toList();

		return PlaceList.builder()
			.total(placeInfoList.size())
			.places(placeInfoList)
			.build();
	}

	public PlaceList findNearestPlace(double lon, double lat) {
		List<Place> placeList = placeRepository.findAll();

		placeList.sort((loc1, loc2) -> {
			double dist1 = calculateDistance(lat, lon, loc1.getLatitude(), loc1.getLongitude());
			double dist2 = calculateDistance(lat, lon, loc2.getLatitude(), loc2.getLongitude());
			return Double.compare(dist1, dist2);
		});

		List<PlaceInfo> placeInfoList = placeList.stream()
			.map(place -> PlaceInfo.builder()
				.place(place)
				.build())
			.toList();
		
		return PlaceList.builder()
			.places(placeInfoList)
			.build();
	}

	private List<Place> getPlaceByActivity(Activity activity) {
		switch (activity) {
			case SNORKELING -> {
				return placeRepository.findAllByCanSnorkelingIsTrue();
			}
			case DIVING -> {
				return placeRepository.findAllByCanDivingIsTrue();
			}
			case SWIMMING -> {
				return placeRepository.findAllByCanSwimmingIsTrue();
			}
			case SURFING -> {
				return placeRepository.findAllByCanSurfingIsTrue();
			}
			case KAYAKING_AND_PADDLE_BOARDING -> {
				return placeRepository.findAllByCanKayakingPaddleBoardingIsTrue();
			}
			default -> {
				return new ArrayList<>();
			}
		}
	}

	private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
		// Haversine 공식을 사용한 거리 계산 로직
		double earthRadius = 6371; // 킬로미터
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
			Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
				Math.sin(dLon / 2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return earthRadius * c;
	}
}
