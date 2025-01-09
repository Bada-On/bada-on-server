package nambang_swag.bada_on.response;

import java.util.List;

import lombok.Getter;

@Getter
public class AvailableTime {
	private int date;
	private List<Integer> hours;

	public AvailableTime(int date, List<Integer> hours) {
		this.date = date;
		this.hours = hours;
	}
}
