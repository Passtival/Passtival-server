package com.passtival.backend.global.swagger;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI openAPI() {

		return new OpenAPI()
			.info(new Info()
				.title("Passtival")
				.description("Passtival Server API.")
				.version("v1.0.0"))
			.servers(List.of(
				new Server()
					.url("http://localhost:8080")
					.description("API 서버")
			));
	}

}