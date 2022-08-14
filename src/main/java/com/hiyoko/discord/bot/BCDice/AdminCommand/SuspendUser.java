package com.hiyoko.discord.bot.BCDice.AdminCommand;

import java.util.List;

import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;
import com.hiyoko.discord.bot.BCDice.UserSuspender.UserSuspenderFactory;

public class SuspendUser implements AdminCommand {
	private final Logger logger = LoggerFactory.getLogger(SuspendUser.class);
	private String exec(String id, String reason) {
		String result = UserSuspenderFactory.getUserSuspender().putSuspendUser(id, reason);
		logger.info(result);
		return result;
	}
	
	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client) {
		String id = option.getOptionStringValueByIndex(0).get();
		String reason = option.getOptionStringValueByIndex(1).get();
		return AdminUtil.getSingleMessage(exec(id, reason == null ? "理由未記入" : reason));
	}

	@Override
	public List<String> exec(String option, DiceClient client) {
		String[] tmp = option.split(" ");
		String id = tmp[0];
		String reason = (tmp.length > 1) ? tmp[1] : "理由未記入";
		return AdminUtil.getSingleMessage(exec(id, reason));
	}
}
