package com.hiyoko.discord.bot.BCDice.AdminCommand;

import java.util.Collections;
import java.util.List;

import org.javacord.api.interaction.SlashCommandInteractionOption;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;
import com.hiyoko.discord.bot.BCDice.OriginalDiceBotClients.OriginalDiceBotClientFactory;

public class ListOriginalTable implements AdminCommand {
	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client) {
		return exec("", client);
	}

	@Override
	public List<String> exec(String option, DiceClient client) {
		List<String> result = OriginalDiceBotClientFactory.getOriginalDiceBotClient().getDiceBotList();
		Collections.sort(result);
		return result;
	}

}
