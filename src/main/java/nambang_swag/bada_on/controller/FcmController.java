package nambang_swag.bada_on.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import nambang_swag.bada_on.request.RegisterFcmToken;
import nambang_swag.bada_on.service.AlarmService;

@Hidden
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
@RestController
public class FcmController {

	private final AlarmService alarmService;

	@PostMapping("/token")
	public void registerToken(@RequestBody RegisterFcmToken registerFcmToken) {
		alarmService.registerDevice(registerFcmToken);
	}

	@GetMapping("/test")
	public void alarmTest(@RequestParam String title, @RequestParam String message) {
		alarmService.sendMessage(title, message);
	}
}
