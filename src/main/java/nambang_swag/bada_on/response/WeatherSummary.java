package nambang_swag.bada_on.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class WeatherSummary {
	private int date;
	private int hour;
	private int score;

	@Builder
	public WeatherSummary(int date, int hour, int score) {
		this.date = date;
		this.hour = hour;
		this.score = score;
	}
}
