package nambang_swag.bada_on.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import nambang_swag.bada_on.constant.TideObservatory;
import nambang_swag.bada_on.entity.TideRecord;

public interface TideRepository extends JpaRepository<TideRecord, Long> {

	Optional<TideRecord> findByDateAndTideObservatory(int date, TideObservatory tideObservatory);

	List<TideRecord> findAllByDateAndTideObservatory(int date, TideObservatory tideObservatory);

}
