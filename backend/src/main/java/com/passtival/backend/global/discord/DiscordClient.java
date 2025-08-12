package com.passtival.backend.global.discord;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.passtival.backend.global.discord.dto.DiscordWebhookRequest;

@FeignClient(
	name = "discord-client",
	url = "${discord.webhook.url}",
	configuration = DiscordConfiguration.class
)
public interface DiscordClient {

	@PostMapping
	void sendMessage(@RequestBody DiscordWebhookRequest request);
}
