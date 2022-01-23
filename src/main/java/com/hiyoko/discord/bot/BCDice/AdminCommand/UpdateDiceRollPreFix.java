package com.hiyoko.discord.bot.BCDice.AdminCommand;

import java.util.List;

import org.javacord.api.interaction.SlashCommandInteractionOption;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;

public class UpdateDiceRollPreFix implements AdminCommand {

	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client) {
		return exec("", client);
	}

	@Override
	public List<String> exec(String option, DiceClient client) {
		return client.updateDiceBotsPrefixes();
	}

}
