package nambang_swag.bada_on.config;

import java.io.ByteArrayInputStream;
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
		String firebaseKey = System.getenv("FIREBASE_KEY");
		if (firebaseKey == null || firebaseKey.isEmpty()) {
			throw new IllegalStateException("FIREBASE_KEY 환경 변수가 설정되지 않았습니다.");
		}

		InputStream serviceAccount = new ByteArrayInputStream(firebaseKey.getBytes(StandardCharsets.UTF_8));
		FirebaseOptions options = FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(serviceAccount))
			.build();
		return FirebaseApp.initializeApp(options);
	}
}
