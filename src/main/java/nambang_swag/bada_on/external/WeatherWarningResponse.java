package nambang_swag.bada_on.external;

import java.util.List;

import lombok.Getter;

@Getter
public class WeatherWarningResponse {
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
		private String dayaType;
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
		private String t1; // 제목
		private String t2; // 해당구역
		private String t3; // 발효시각
		private String t4; // 내용
		private String t5; // 특보발효현황시각
		private String t6; // 특보발효현황내용
		private String t7; // 예비특보 발효현황
		private String other;
		private String tmFc;
		private int tmSeq;
		private String warFc;
		private String stnId;
	}
}
