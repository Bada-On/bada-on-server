package nambang_swag.bada_on.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FireBaseConfig {

	@Bean
	public FirebaseApp initializeFirebase() throws IOException {
		FileInputStream serviceAccount = new FileInputStream("/app/firebase-service-account.json");
		FirebaseOptions options = FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(serviceAccount))
			.build();
		return FirebaseApp.initializeApp(options);
	}
}
