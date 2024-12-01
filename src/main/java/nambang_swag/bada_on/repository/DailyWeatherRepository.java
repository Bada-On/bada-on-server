package nambang_swag.bada_on.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import nambang_swag.bada_on.entity.DailyWeather;
import nambang_swag.bada_on.entity.Place;

public interface DailyWeatherRepository extends JpaRepository<DailyWeather, Long> {
	//findByDateAndTimeAndPlace
	Optional<DailyWeather> findByDateAndPlace(int date, Place place);

	@Query("SELECT w FROM DailyWeather w WHERE w.place.id = :placeId AND w.date >= :date")
	List<DailyWeather> findAllByPlaceIdWithDateGreaterThan(Long placeId, int date);
}
