package nambang_swag.bada_on.external;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class TideApiResponse {
	private Result result;

	@Getter
	public static class Result {
		private List<TideData> data;
		private Meta meta;
	}

	@Getter
	public static class TideData {
		@JsonProperty("tph_level")
		private Integer tphLevel; // Integer로 변경

		@JsonProperty("tph_time")
		private String tphTime;

		@JsonProperty("hl_code")
		private String hlCode;
	}

	@Getter
	public static class Meta {
		@JsonProperty("obs_post_id")
		private String obsPostId;

		@JsonProperty("obs_last_req_cnt")
		private String obsLastReqCnt;

		@JsonProperty("obs_lat")
		private Double obsLat; // Double로 변경

		@JsonProperty("obs_post_name")
		private String obsPostName;

		@JsonProperty("obs_lon")
		private Double obsLon; // Double로 변경
	}
}