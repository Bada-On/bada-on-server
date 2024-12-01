package nambang_swag.bada_on;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BadaOnServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BadaOnServerApplication.class, args);
	}

}
