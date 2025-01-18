package nambang_swag.bada_on.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import nambang_swag.bada_on.constant.WarningCode;
import nambang_swag.bada_on.constant.WarningLevel;
import nambang_swag.bada_on.constant.WarningRegion;
import nambang_swag.bada_on.entity.Warning;
import nambang_swag.bada_on.entity.WarningStatus;

public interface WarningRepository extends JpaRepository<Warning, Long> {

	Optional<Warning> findByRegionAndCodeAndLevelAndStatusIn(
		WarningRegion region,
		WarningCode code,
		WarningLevel level,
		List<WarningStatus> statuses
	);

	List<Warning> findAllByRegionAndStatusIn(WarningRegion region, List<WarningStatus> statuses);

	List<Warning> findAllByStatusIn(List<WarningStatus> statuses);
}
