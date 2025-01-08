package nambang_swag.bada_on.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nambang_swag.bada_on.request.PlaceRegister;
import nambang_swag.bada_on.service.ExternalApiService;
import nambang_swag.bada_on.service.PlaceService;

@RequiredArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {

	private final PlaceService placeService;
	private final ExternalApiService externalApiService;

	@Override
	public void run(String... args) {
		List<String> commonActivities = Arrays.asList("diving", "snorkeling", "swimming", "surfing",
			"kayakingPaddleBoarding");

		List<PlaceRegister> places = new ArrayList<>();
		places.add(new PlaceRegister("협재 해수욕장", 33.394, 126.239, commonActivities));
		places.add(new PlaceRegister("금능으뜸원 해수욕장", 33.389, 126.235, commonActivities));
		places.add(new PlaceRegister("이호테우 해수욕장", 33.497, 126.452, commonActivities));
		places.add(new PlaceRegister("삼양검은모래 해수욕장", 33.525, 126.586, commonActivities));
		places.add(new PlaceRegister("함덕서우봉 해수욕장", 33.543, 126.669, commonActivities));
		places.add(new PlaceRegister("김녕성세기 해수욕장", 33.557, 126.759, commonActivities));
		places.add(new PlaceRegister("하고수동 해수욕장", 33.513, 126.958, commonActivities));
		places.add(new PlaceRegister("중문ㆍ색달 해수욕장", 33.244, 126.411, commonActivities));
		places.add(new PlaceRegister("화순금모래 해수욕장", 33.236, 126.319, commonActivities));
		places.add(new PlaceRegister("하효쇠소깍 해수욕장", 33.252, 126.623, commonActivities));
		places.add(new PlaceRegister("표선해비치", 33.327, 126.843, commonActivities));
		places.add(new PlaceRegister("신양섭지코지 해수욕장", 33.434, 126.923, commonActivities));
		places.add(new PlaceRegister("종달 해수욕장", 33.496, 126.913, commonActivities));
		places.add(new PlaceRegister("하도 해수욕장", 33.512, 126.898, commonActivities));

		for (PlaceRegister place : places) {
			placeService.register(place);
		}
	}
}
