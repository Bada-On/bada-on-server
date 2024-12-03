package nambang_swag.bada_on.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
public enum Message {
	SNORKELING(
		"snorkeling",
		"스노클링 천국이에요!",
		"스노클링하기에 무난한 조건이에요.",
		"스노클링하기에는 위험할 수 있어요."),
	DIVING(
		"diving",
		"다이빙 최적의 날이에요!",
		"다이빙은 가능해요.",
		"다이빙이 제한될 수 있어요."),
	SWIMMING(
		"swimming",
		"수영하기 완벽해요!",
		"수영하기에 적당해요.",
		"수영이 위험할 수 있어요."),
	SURFING(
		"surfing",
		"서핑 최고의 컨디션이에요!",
		"서핑하기에 좋아요.",
		"서핑하기 어려워요."),
	KAYAKING_AND_PADDLE_BOARDING(
		"kayakingPaddleBoarding",
		"카약/패들보드 천국이에요!",
		"카약/패들보드를 하기 무난한 조건이에요.",
		"카약/패들보드는 위험할 수 있어요."
	);

	private final String value;
	private final String good;
	private final String mid;
	private final String bad;

	Message(String value, String good, String mid, String bad) {
		this.value = value;
		this.good = good;
		this.mid = mid;
		this.bad = bad;
	}

	public static String from(String value, int score) {
		for (Message message : Message.values()) {
			if (message.getValue().equals(value)) {
				if (score >= 80) {
					return message.good;
				} else if (score >= 50) {
					return message.mid;
				} else {
					return message.bad;
				}

			}
		}
		return "";
	}
}
