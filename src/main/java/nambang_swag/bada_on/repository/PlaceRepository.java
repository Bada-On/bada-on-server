package nambang_swag.bada_on.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import nambang_swag.bada_on.entity.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

	Optional<Place> findByName(String name);

	List<Place> findAllByCanSnorkelingIsTrue();

	List<Place> findAllByCanDivingIsTrue();

	List<Place> findAllByCanSwimmingIsTrue();

	List<Place> findAllByCanSurfingIsTrue();

	List<Place> findAllByCanKayakingPaddleBoardingIsTrue();
}
