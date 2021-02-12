package com.hiyoko.discord.bot.BCDice.ChatTool;

import org.javacord.api.DiscordApi;

public class ChatToolClientFactory {
	public static ChatToolClient getChatToolClient(DiscordApi api) {
		return new DiscordClient(api);
	}
}
