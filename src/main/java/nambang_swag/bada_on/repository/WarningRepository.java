package nambang_swag.bada_on.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

	@Query("""
			SELECT w FROM Warning w
			WHERE (w.region = :landRegion OR w.region =:seaRegion)
			AND w.status IN :statuses
		""")
	List<Warning> findAllByPlaceRegionAndStatusIn(
		@Param("landRegion") WarningRegion landRegion,
		@Param("seaRegion") WarningRegion seaRegion,
		@Param("statuses") List<WarningStatus> statuses
	);
}
