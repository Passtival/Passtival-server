package com.passtival.backend.global.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

	@Value("${swagger.server.url:http://localhost:8080}")
	private String serverUrl;

	@Value("${swagger.server.description:API 서버}")
	private String serverDescription;

	@Bean
	public OpenAPI openAPI() {

		String jwtSchemeName = "jwtAuth";

		Components components = new Components()
			.addSecuritySchemes(jwtSchemeName,
				new SecurityScheme()
					.name(jwtSchemeName)
					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer")
					.bearerFormat("JWT"));

		return new OpenAPI()
			.info(new Info()
				.title("Passtival")
				.description("Passtival Server API.")
				.version("v1.0.0"))
			.servers(List.of(
				new Server()
					.url(serverUrl)
					.description(serverDescription)
			))
			.components(components);
	}

}