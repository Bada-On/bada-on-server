package nambang_swag.bada_on.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import nambang_swag.bada_on.entity.TideRecord;
import nambang_swag.bada_on.constant.TideObservatory;

public interface TideRepository extends JpaRepository<TideRecord, Long> {

	List<TideRecord> findAllByDateAndTideObservatory(int date, TideObservatory tideObservatory);

	@Query("Select t FROM TideRecord t WHERE t.date >= :date")
	List<TideRecord> findTideRecordWithDateGreaterThan(@Param("date") int date);
}
