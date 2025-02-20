package nambang_swag.bada_on.external;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Getter;

@Getter
@JacksonXmlRootElement(localName = "response")
public class SunRiseSetForeCastApiResponse {
	@JacksonXmlProperty(localName = "header")
	private Header header;

	@JacksonXmlProperty(localName = "body")
	private Body body;

	@Getter
	public static class Header {
		private String resultCode;
		private String resultMsg;
	}

	@Getter
	public static class Body {
		private Items items;
		private int numOfRows;
		private int pageNo;
		private int totalCount;
	}

	@Getter
	public static class Items {
		@JacksonXmlElementWrapper(useWrapping = false)
		@JacksonXmlProperty(localName = "item")
		private List<Item> item;
	}

	@Getter
	public static class Item {
		private String aste;
		private String astm;
		private String civile;
		private String civilm;
		private String latitude;
		private double latitudeNum;
		private String location;
		private String locdate;
		private String longitude;
		private double longitudeNum;
		private String moonrise;
		private String moonset;
		private String moontransit;
		private String naute;
		private String nautm;
		private String sunrise;
		private String sunset;
		private String suntransit;
	}
}