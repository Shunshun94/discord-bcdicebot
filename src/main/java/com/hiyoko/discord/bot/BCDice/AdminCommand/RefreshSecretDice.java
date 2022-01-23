package com.hiyoko.discord.bot.BCDice.AdminCommand;

import java.util.List;

import org.javacord.api.interaction.SlashCommandInteractionOption;

import com.hiyoko.discord.bot.BCDice.BCDiceCLI;
import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;

public class RefreshSecretDice implements AdminCommand {
	private final BCDiceCLI cli;
	public RefreshSecretDice(BCDiceCLI cli) {
		this.cli = cli;
	}
	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client) {
		return AdminUtil.getSingleMessage(cli.refreshSecretMessages());
	}
}
