package nambang_swag.bada_on.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import nambang_swag.bada_on.util.validation.annotation.ValidLatitude;
import nambang_swag.bada_on.util.validation.annotation.ValidLongitude;

@Getter
public class PlaceRegister {

	@NotBlank(message = "이름을 입력해주세요.")
	private String name;

	@NotNull(message = "등록할 장소의 위도를 입력해주세요.")
	@ValidLatitude(message = "위도는 30에서 45 사이의 값이어야 하며, XX.XX 형식이어야 합니다.")
	private Double latitude;

	@NotNull(message = "등록할 장소의 경도를 입력해주세요.")
	@ValidLongitude(message = "경도는 120에서 140 사이의 값이어야 하며, XXX.XX 형식이어야 합니다.")
	private Double longitude;

	private List<String> activities;

	public PlaceRegister(String name, Double latitude, Double longitude, List<String> activities) {
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.activities = activities;
	}
}
