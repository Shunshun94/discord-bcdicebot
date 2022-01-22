package com.hiyoko.discord.bot.BCDice.AdminCommand;

import java.util.List;

import org.javacord.api.interaction.SlashCommandInteractionOption;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;

public interface AdminCommand {
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client);
}
