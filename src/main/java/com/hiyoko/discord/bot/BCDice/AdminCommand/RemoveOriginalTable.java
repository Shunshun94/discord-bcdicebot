package com.hiyoko.discord.bot.BCDice.AdminCommand;

import java.io.IOException;
import java.util.List;

import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;
import com.hiyoko.discord.bot.BCDice.OriginalDiceBotClients.OriginalDiceBotClientFactory;

public class RemoveOriginalTable implements AdminCommand {
	final Logger logger = LoggerFactory.getLogger(RemoveOriginalTable.class);
	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client) {
		String name = option.getOptionByIndex(0).get().getStringValue().get();
		try {
			OriginalDiceBotClientFactory.getOriginalDiceBotClient().unregisterDiceBot(name);
			return AdminUtil.getSingleMessage(String.format("ダイスボット表 [%s] を削除しました", name));
		} catch (IOException e) {
			String msg = String.format("ダイスボット表 [%s] の削除に失敗しました", name);
			logger.warn(msg, e);
			return AdminUtil.getSingleMessage(msg);
		}
	}

}
