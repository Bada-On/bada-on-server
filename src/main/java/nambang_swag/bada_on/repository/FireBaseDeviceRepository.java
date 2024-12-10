package nambang_swag.bada_on.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import nambang_swag.bada_on.entity.FireBaseDevice;

public interface FireBaseDeviceRepository extends JpaRepository<FireBaseDevice, Long> {

	Optional<FireBaseDevice> findByToken(String token);
}
