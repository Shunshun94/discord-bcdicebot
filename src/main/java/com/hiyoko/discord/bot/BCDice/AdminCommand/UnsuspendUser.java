package com.hiyoko.discord.bot.BCDice.AdminCommand;

import java.util.List;

import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;
import com.hiyoko.discord.bot.BCDice.UserSuspender.UserSuspenderFactory;

public class UnsuspendUser implements AdminCommand {
	private final Logger logger = LoggerFactory.getLogger(UnsuspendUser.class);
	private String exec(String id) {
		String result = UserSuspenderFactory.getUserSuspender().removeSuspendedUser(id);
		logger.info(result);
		return result;
	}

	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client) {
		String id = option.getOptionStringValueByIndex(0).get();
		return AdminUtil.getSingleMessage(exec(id));
	}

	@Override
	public List<String> exec(String option, DiceClient client) {
		return AdminUtil.getSingleMessage(exec(option));
	}

}
