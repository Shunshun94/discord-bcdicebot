package com.hiyoko.discord.bot.BCDice.Listener;

import org.javacord.api.DiscordApi;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandBuilder;

import com.hiyoko.discord.bot.BCDice.BCDiceCLI;

public class SlashInputConfig {
	private final DiscordApi api;
	private final BCDiceCLI cli;
	private String prefix;
	private String shortPrefix;
	private String testServerId;
	private boolean isActiveOriginalTableSuggestion;
	public SlashInputConfig(
		DiscordApi api,
		BCDiceCLI cli,
		String prefix,
		String shortPrefix,
		String testServerId,
		boolean isActiveOriginalTableSuggestion
	) {
		this.api = api;
		this.cli = cli;
		this.prefix = prefix.isEmpty() ? "bcdice" : (prefix.startsWith("/") ? prefix.substring(1) : prefix);
		this.shortPrefix = shortPrefix.isEmpty() ? "br" : (shortPrefix.startsWith("/") ? shortPrefix.substring(1) : shortPrefix);
		this.testServerId = testServerId;
		this.isActiveOriginalTableSuggestion = isActiveOriginalTableSuggestion;
	}

	public DiscordApi getDiscordApi() {
		return api;
	}

	public BCDiceCLI getCli() {
		return cli;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getShortPreifx() {
		return shortPrefix;
	}

	public SlashCommand joinSlashCommand(SlashCommandBuilder commandBuilder) {
		if(testServerId.isEmpty()) {
			return commandBuilder.createGlobal(api).join();
		} else {
			return commandBuilder.createForServer(api.getServerById(testServerId).get()).join();
		}
	}

	public boolean isActiveOriginalTableSuggestion() {
		return isActiveOriginalTableSuggestion;
	}

	public String toString() {
		return String.format("{\"name\":\"%s\", \"prefix\":\"%s\", \"shortPrefix\":\"%s\", \"serverId\":\"%s\"}",
			api.getYourself().getName(), this.prefix, this.shortPrefix, testServerId.isEmpty() ? "global" : this.testServerId
		);
	}
}
