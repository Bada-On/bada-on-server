package nambang_swag.bada_on.external;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class WaterTemperatureForecastApiResponse {
	private Result result;

	@Getter
	public static class Result {
		private List<WaterTemperatureData> data;
		private TideApiResponse.Meta meta;
	}

	@Getter
	public static class WaterTemperatureData {
		@JsonProperty("hour")
		private Integer hour; // Integer로 변경

		@JsonProperty("date")
		private Integer date;

		@JsonProperty("temperature")
		private Float temperature;
	}

	@Getter
	public static class Meta {
		@JsonProperty("obs_last_req_cnt")
		private String obsLastReqCnt;

		@JsonProperty("obs_lat")
		private Double obsLat; // Double로 변경

		@JsonProperty("obs_lon")
		private Double obsLon; // Double로 변경
	}
}
