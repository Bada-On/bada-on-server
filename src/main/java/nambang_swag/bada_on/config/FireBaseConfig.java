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
		String firebaseKey = System.getenv("FIREBASE_KEY");
		File keyFile = new File("/app/config/firebase-key.json");

		if (firebaseKey != null && !firebaseKey.isEmpty()) {
			// 환경 변수에서 키를 읽는 경우
			InputStream serviceAccount = new ByteArrayInputStream(firebaseKey.getBytes(StandardCharsets.UTF_8));
			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();
			return FirebaseApp.initializeApp(options);
		} else if (keyFile.exists()) {
			// 파일에서 키를 읽는 경우
			FileInputStream serviceAccount = new FileInputStream(keyFile);
			FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.build();
			return FirebaseApp.initializeApp(options);
		} else {
			throw new IllegalStateException("Firebase 인증 정보를 찾을 수 없습니다.");
		}
	}
}
