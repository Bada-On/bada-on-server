package nambang_swag.bada_on.util;

import org.springframework.stereotype.Component;

/**
 * 기상청 좌표계 변환 유틸리티
 * Lambert Conformal Conic Projection을 사용하여 위경도와 격자 좌표를 상호 변환
 */
@Component
public class CoordinateConverter {

	private static final int NX = 149; // X축 격자점 수
	private static final int NY = 253; // Y축 격자점 수

	private final LamcParameter map;

	public CoordinateConverter() {
		this.map = new LamcParameter();
	}

	/**
	 * 위경도를 격자 좌표로 변환
	 * @param lon 경도
	 * @param lat 위도
	 * @return 격자 좌표 [x, y]
	 */
	public int[] convertToGrid(double lat, double lon) {
		validateLonLat(lat, lon);
		double[] grid = lamcproj(lon, lat, 0);  // 순서를 유지해 lamcproj 호출
		return new int[] {(int)(grid[0] + 1.5), (int)(grid[1] + 1.5)};
	}

	/**
	 * 격자 좌표를 위경도로 변환
	 * @param x X 격자 좌표
	 * @param y Y 격자 좌표
	 * @return 위경도 [위도, 경도]
	 */
	public double[] convertToLonLat(int x, int y) {
		validateGrid(x, y);
		double[] lonLat = lamcproj(x - 1, y - 1, 1);
		// 위도와 경도의 순서를 바꿔서 [위도, 경도] 형태로 반환
		return new double[] {lonLat[1], lonLat[0]};
	}

	private void validateGrid(int x, int y) {
		if (x < 1 || x > NX || y < 1 || y > NY) {
			throw new IllegalArgumentException(
				String.format("Grid coordinates out of range. X should be in [1,%d] and Y in [1,%d]", NX, NY)
			);
		}
	}

	private void validateLonLat(double lat, double lon) {
		if (lon < 120 || lon > 140 || lat < 30 || lat > 45) {
			throw new IllegalArgumentException(
				"Coordinates out of range. Latitude should be in [30,45] and Longitude in [120,140]"
			);
		}
	}

	private double[] lamcproj(double first, double second, int code) {
		double PI = Math.asin(1.0) * 2.0;
		double DEGRAD = PI / 180.0;
		double RADDEG = 180.0 / PI;

		double re = map.Re / map.grid;
		double slat1 = map.slat1 * DEGRAD;
		double slat2 = map.slat2 * DEGRAD;
		double olon = map.olon * DEGRAD;
		double olat = map.olat * DEGRAD;

		double sn = Math.tan(PI * 0.25 + slat2 * 0.5) / Math.tan(PI * 0.25 + slat1 * 0.5);
		sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);

		double sf = Math.tan(PI * 0.25 + slat1 * 0.5);
		sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;

		double ro = Math.tan(PI * 0.25 + olat * 0.5);
		ro = re * sf / Math.pow(ro, sn);

		double[] result = new double[2];

		if (code == 0) {  // 위경도 -> 격자
			double ra = Math.tan(PI * 0.25 + second * DEGRAD * 0.5);
			ra = re * sf / Math.pow(ra, sn);
			double theta = first * DEGRAD - olon;

			if (theta > PI)
				theta -= 2.0 * PI;
			if (theta < -PI)
				theta += 2.0 * PI;

			theta *= sn;
			result[0] = ra * Math.sin(theta) + map.xo;
			result[1] = ro - ra * Math.cos(theta) + map.yo;
		} else {  // 격자 -> 위경도
			double xn = first - map.xo;
			double yn = ro - second + map.yo;
			double ra = Math.sqrt(xn * xn + yn * yn);
			if (sn < 0.0)
				ra = -ra;

			double alat = Math.pow((re * sf / ra), (1.0 / sn));
			alat = 2.0 * Math.atan(alat) - PI * 0.5;

			double theta;
			if (Math.abs(xn) <= 0.0) {
				theta = 0.0;
			} else {
				if (Math.abs(yn) <= 0.0) {
					theta = PI * 0.5;
					if (xn < 0.0)
						theta = -theta;
				} else {
					theta = Math.atan2(xn, yn);
				}
			}

			double alon = theta / sn + olon;
			result[0] = alon * RADDEG;
			result[1] = alat * RADDEG;
		}

		return result;
	}

	private static class LamcParameter {
		final double Re = 6371.00877;    // 지구 반경(km)
		final double grid = 5.0;         // 격자간격(km)
		final double slat1 = 30.0;       // 표준위도 1
		final double slat2 = 60.0;       // 표준위도 2
		final double olon = 126.0;       // 기준점 경도
		final double olat = 38.0;        // 기준점 위도
		final double xo = 210 / grid;    // 기준점 X좌표
		final double yo = 675 / grid;    // 기준점 Y좌표
	}
}
