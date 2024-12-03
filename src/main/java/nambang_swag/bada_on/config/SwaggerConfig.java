package nambang_swag.bada_on.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@OpenAPIDefinition(
	servers = {
		@Server(url = "https://badaon.shop", description = "개발 서버"),
		@Server(url = "http://localhost:8080", description = "로컬 서버")
	}
)
@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("Bada On API")
				.version("v1.0")
				.description("바다온 API 명세서"));
	}
}
