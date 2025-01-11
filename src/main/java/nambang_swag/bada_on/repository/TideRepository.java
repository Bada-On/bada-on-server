package nambang_swag.bada_on.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import nambang_swag.bada_on.constant.TideObservatory;
import nambang_swag.bada_on.entity.TideRecord;

public interface TideRepository extends JpaRepository<TideRecord, Long> {

	Optional<TideRecord> findByDateAndTideObservatoryAndTidalTime(int date, TideObservatory tideObservatory,
		LocalDateTime time);

	List<TideRecord> findAllByDateAndTideObservatory(int date, TideObservatory tideObservatory);

	@Query("SELECT t FROM TideRecord t WHERE t.date IN :dates AND t.tideObservatory = :tideObservatory")
	List<TideRecord> findAllByDatesAndTideObservatory(@Param("dates") List<Integer> dates,
		@Param("tideObservatory") TideObservatory tideObservatory);
}
