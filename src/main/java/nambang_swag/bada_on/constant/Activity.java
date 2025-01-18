package nambang_swag.bada_on.constant;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
public enum Activity {
	SNORKELING("snorkeling"),
	DIVING("diving"),
	SWIMMING("swimming"),
	SURFING("surfing"),
	PADDlING("paddling");

	private final String value;

	Activity(String value) {
		this.value = value;
	}

	@JsonCreator
	public static Activity from(String sub) {
		for (Activity activity : Activity.values()) {
			if (activity.getValue().equals(sub)) {
				return activity;
			}
		}
		return null;
	}
}

