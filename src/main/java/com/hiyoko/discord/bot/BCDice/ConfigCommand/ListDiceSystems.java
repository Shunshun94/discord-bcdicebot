package com.hiyoko.discord.bot.BCDice.ConfigCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;

public class ListDiceSystems implements ConfigCommand {


	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client, User user) {
		List<String> resultList = new ArrayList<String>();
		resultList.add("[DiceBot List]");
		try {
			resultList.addAll(ConfigUtil.separateStringWithLengthLimitation(client.getSystems().getSystemList(), 1000));
			return resultList;
		} catch (IOException e) {
			return ConfigUtil.getSingleMessage(e.getMessage());
		}
	}

}
