package nambang_swag.bada_on.external;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WeatherNowCastApiResponse {
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
		private String obsrValue;
		private int nx;
		private int ny;
	}
}
