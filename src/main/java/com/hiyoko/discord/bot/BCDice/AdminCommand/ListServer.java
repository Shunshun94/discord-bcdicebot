package com.hiyoko.discord.bot.BCDice.AdminCommand;

import java.util.List;

import org.javacord.api.interaction.SlashCommandInteractionOption;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;

public class ListServer implements AdminCommand {
	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client) {
		return client.getDiceUrlList();
	}
}
