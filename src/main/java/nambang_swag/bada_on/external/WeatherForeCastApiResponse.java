package nambang_swag.bada_on.external;

import java.util.List;

import lombok.Getter;

@Getter
public class WeatherForeCastApiResponse {
	private Response response;

	@Getter
	public static class Response {
		private Header header;
		private Body body;
	}

	@Getter
	public static class Header {
		private String resultCode;
		private String resultMsg;
	}

	@Getter
	public static class Body {
		private String dataType;
		private Items items;
		private int pageNo;
		private int numOfRows;
		private int totalCount;
	}

	@Getter
	public static class Items {
		private List<Item> item;
	}

	@Getter
	public static class Item {
		private int baseDate;
		private int baseTime;
		private String category;
		private int fcstDate;
		private int fcstTime;
		private String fcstValue;
		private int nx;
		private int ny;
	}
}
