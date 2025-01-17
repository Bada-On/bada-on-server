package nambang_swag.bada_on.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nambang_swag.bada_on.request.PlaceRegister;
import nambang_swag.bada_on.service.PlaceService;

@RequiredArgsConstructor
@Component
public class DataLoader implements CommandLineRunner {

	private final PlaceService placeService;

	@Override
	public void run(String... args) {
		List<String> commonActivities = Arrays.asList("diving", "snorkeling", "swimming", "surfing",
			"padding");

		List<PlaceRegister> places = new ArrayList<>();
		places.add(new PlaceRegister("협재 해수욕장", 33.394, 126.239, "제주 제주시 한림읍 한림로 329-10", commonActivities));
		places.add(new PlaceRegister("금능으뜸원 해수욕장", 33.389, 126.235, "제주 제주시 한림읍 금능길 119-10", commonActivities));
		places.add(new PlaceRegister("이호테우 해수욕장", 33.497, 126.452, "제주 제주시 도리로 20", commonActivities));
		places.add(new PlaceRegister("삼양검은모래 해수욕장", 33.525, 126.586, "제주 제주시 삼양동", commonActivities));
		places.add(new PlaceRegister("함덕서우봉 해수욕장", 33.543, 126.669, "제주 제주시 조천읍 조함해안로 525", commonActivities));
		places.add(new PlaceRegister("김녕성세기 해수욕장", 33.557, 126.759, "제주 제주시 구좌읍 김녕로21길 25", commonActivities));
		places.add(new PlaceRegister("하고수동 해수욕장", 33.513, 126.958, "제주 제주시 우도면 연평리", commonActivities));
		places.add(new PlaceRegister("중문ㆍ색달 해수욕장", 33.244, 126.411, "제주 서귀포시 중문관광로72번길 100", commonActivities));
		places.add(new PlaceRegister("화순금모래 해수욕장", 33.236, 126.319, "제주 서귀포시 안덕면 화순해안로 91", commonActivities));
		places.add(new PlaceRegister("하효쇠소깍 해수욕장", 33.252, 126.623, "제주 서귀포시 하효동", commonActivities));
		places.add(new PlaceRegister("표선해비치", 33.327, 126.843, "제주 서귀포시 표선면 표선리", commonActivities));
		places.add(new PlaceRegister("신양섭지코지 해수욕장", 33.434, 126.923, "제주 서귀포시 성산읍 섭지코지로 107", commonActivities));
		places.add(new PlaceRegister("종달 해수욕장", 33.496, 126.913, "제주 제주시 구좌읍 종달리 565-72", commonActivities));
		places.add(new PlaceRegister("하도 해수욕장", 33.512, 126.898, "제주 제주시 구좌읍 하도리", commonActivities));

		for (PlaceRegister place : places) {
			placeService.register(place);
		}
	}
}
