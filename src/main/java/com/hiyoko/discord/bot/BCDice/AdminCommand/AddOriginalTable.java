package com.hiyoko.discord.bot.BCDice.AdminCommand;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.javacord.api.entity.Attachment;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;
import com.hiyoko.discord.bot.BCDice.OriginalDiceBotClients.OriginalDiceBotClientFactory;

public class AddOriginalTable implements AdminCommand {
	final Logger logger = LoggerFactory.getLogger(AddOriginalTable.class);

	private List<String> exec(URL url, String name) {
		try {
			OriginalDiceBotClientFactory.getOriginalDiceBotClient().registerDiceBot(url, name);
			String msg = String.format("ダイスボット表 [%s] を登録しました", name);
			return AdminUtil.getSingleMessage(msg);
		} catch (IOException e) {
			String msg = String.format("ダイスボット表 [%s] の登録に失敗しました", name);
			logger.warn(msg, e);
			return AdminUtil.getSingleMessage(msg);
		}
	}
	
	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client) {
		Attachment file = option.getArgumentAttachmentValueByIndex(0).get();
		URL url = file.getUrl();
		String name = option.getArgumentStringValueByIndex(1).orElse(file.getFileName().split("\\.")[0]);
		return exec(url, name);
	}

	@Override
	public List<String> exec(String option, DiceClient client) {
		String[] tmp = option.split(" ");
		try {
			return exec(new URL(tmp[0]), tmp[1]);
		} catch (MalformedURLException e) {
			String msg = String.format("ダイスボット表 [%s] の登録に失敗しました", tmp[1]);
			logger.warn(msg, e);
			return AdminUtil.getSingleMessage(msg);
		}
	}

}
