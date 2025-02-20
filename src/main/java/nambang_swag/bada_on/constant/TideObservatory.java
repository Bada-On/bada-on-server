package nambang_swag.bada_on.constant;

import lombok.Getter;

@Getter
public enum TideObservatory {

	JEJU("DT_0004", "제주", 33.527, 126.543),
	SEOGWIPO("DT_0010", "서귀포", 33.24, 126.561),
	SEONGSANPO("DT_0022", "성산포", 33.474, 126.927),
	MOSEULPO("DT_0023", "모슬포", 33.214, 126.251);

	private final String code;
	private final String name;
	private final double latitude;
	private final double longitude;

	TideObservatory(String code, String name, double latitude, double longitude) {
		this.code = code;
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public static TideObservatory findNearest(double lat, double lon) {
		TideObservatory nearest = null;
		double minDistance = Double.MAX_VALUE;

		for (TideObservatory observatory : values()) {
			double distance = calculateDistance(lat, lon,
				observatory.latitude, observatory.longitude);
			if (distance < minDistance) {
				minDistance = distance;
				nearest = observatory;
			}
		}
		return nearest;
	}

	private static double calculateDistance(double lat1, double lon1,
		double lat2, double lon2) {
		return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2));
	}
}
