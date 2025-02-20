package nambang_swag.bada_on.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import nambang_swag.bada_on.constant.WarningRegion;
import nambang_swag.bada_on.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

	List<Place> findAllByCanSnorkelingIsTrue();

	List<Place> findAllByCanDivingIsTrue();

	List<Place> findAllByCanSwimmingIsTrue();

	List<Place> findAllByCanSurfingIsTrue();

	List<Place> findAllByCanPaddlingIsTrue();

	@Query("SELECT p FROM Place p WHERE p.landRegion = :region OR p.seaRegion = :region")
	List<Place> findAllByLandRegionOrSeaRegion(@Param("region") WarningRegion region);
}
