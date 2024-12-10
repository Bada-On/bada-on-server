package nambang_swag.bada_on.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nambang_swag.bada_on.entity.FireBaseDevice;
import nambang_swag.bada_on.repository.FireBaseDeviceRepository;
import nambang_swag.bada_on.request.RegisterFcmToken;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlarmService {

	private final FireBaseDeviceRepository deviceRepository;
	private final FireBaseDeviceRepository fireBaseDeviceRepository;

	public void registerDevice(RegisterFcmToken request) {
		Optional<FireBaseDevice> device = fireBaseDeviceRepository.findByToken(request.getToken());
		if (device.isPresent()) {
			return;
		}

		FireBaseDevice fireBaseDevice = FireBaseDevice.builder()
			.token(request.getToken())
			.device(request.getDevice())
			.build();
		deviceRepository.save(fireBaseDevice);
	}

	public void sendMessage(String title, String content) {
		List<FireBaseDevice> fireBaseDeviceList = deviceRepository.findAll();
		for (FireBaseDevice fireBaseDevice : fireBaseDeviceList) {
			try {
				Message message = Message.builder()
					.putData("title", title)
					.putData("content", content)
					.setToken(fireBaseDevice.getToken())
					.build();
				FirebaseMessaging.getInstance().send(message);
			} catch (Exception e) {
				log.info("messageSendError: {}", e.getMessage());
			}
		}
	}
}
