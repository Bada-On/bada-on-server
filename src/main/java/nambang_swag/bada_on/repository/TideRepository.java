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

	@Query("SELECT t FROM TideRecord t WHERE t.date >= :first AND t.date <= :last AND t.tideObservatory = :tideObservatory")
	List<TideRecord> findAllByDatesAndTideObservatory(@Param("first") int first, @Param("last") int last,
		@Param("tideObservatory") TideObservatory tideObservatory);
}
