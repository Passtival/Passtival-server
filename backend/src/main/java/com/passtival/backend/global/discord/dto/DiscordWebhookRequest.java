package com.passtival.backend.global.discord.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DiscordWebhookRequest {

	private String content;
	private String username;
	private String avatar_url;
}
