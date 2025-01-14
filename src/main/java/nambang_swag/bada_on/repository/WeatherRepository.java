package nambang_swag.bada_on.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import nambang_swag.bada_on.entity.Place;
import nambang_swag.bada_on.entity.Weather;

public interface WeatherRepository extends JpaRepository<Weather, Integer> {

	Optional<Weather> findByDateAndTimeAndPlace(int date, int time, Place place);

	@Query("SELECT w FROM Weather w WHERE w.date >= :date AND w.isUpdated = true")
	List<Weather> getWeatherIsUpdated(@Param("date") int date, @Param("time") int time);

}
