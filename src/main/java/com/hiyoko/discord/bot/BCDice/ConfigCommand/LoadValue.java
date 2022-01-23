package com.hiyoko.discord.bot.BCDice.ConfigCommand;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiyoko.discord.bot.BCDice.BCDiceCLI;
import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;
import com.hiyoko.discord.bot.BCDice.DiceClient.SavedMessageFactory;
import com.hiyoko.discord.bot.BCDice.dto.SecretMessage;

public class LoadValue implements ConfigCommand {
	final Logger logger = LoggerFactory.getLogger(LoadValue.class);
	private List<String> getMessage(String id, String index) throws IOException {
		try {
			Map<String, SecretMessage> list = SavedMessageFactory.getSavedMessages().get(id);
			return list.get(index).getMessages();
		} catch (Exception e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client, User user, Channel channel) {
		String key = option.getOptionByIndex(0).get().getStringValue().get();
		try {
			return getMessage(user.getIdAsString(), key);
		} catch (IOException e) {
			String msg = String.format("[%s] に該当するメッセージは見つかりませんでした", key);
			logger.info(String.format("%s(%s) %s", user.getIdAsString(), user.getName(), msg), e);
			return ConfigUtil.getSingleMessage(msg);
		}
	}
}
