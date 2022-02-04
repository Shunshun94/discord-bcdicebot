package com.hiyoko.discord.bot.BCDice.ConfigCommand;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;
import com.hiyoko.discord.bot.BCDice.dto.SecretMessage;

public class LoadValue implements ConfigCommand {
	private final Logger logger = LoggerFactory.getLogger(LoadValue.class);
	private final Map<String, Map<String, SecretMessage>> savedMessages;
	public LoadValue(Map<String, Map<String, SecretMessage>> map) {
		savedMessages = map;
	}
	private List<String> getMessage(String id, String index) throws IOException {
		Map<String, SecretMessage> list = savedMessages.get(id);
		if(list == null) {
			String comment = String.format("ユーザ [%s] の情報が見つかりませんでした", id);
			logger.warn(savedMessages.toString());
			throw new IOException(comment);
		} 
		try {
			return list.get(index).getMessages();
		} catch (NullPointerException e) {
			String comment = String.format("ユーザ [%s] の情報は見つかりましたが、インデックス [%s] の値は見つかりませんでした", id, index);
			throw new IOException(comment, e);
		}
	}

	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client, User user, Channel channel) {
		String key = option.getOptionByIndex(0).get().getStringValue().get();
		return exec(key, client, user.getIdAsString(), channel.getIdAsString());
	}

	@Override
	public List<String> exec(String key, DiceClient client, String user, String channel) {
		try {
			return getMessage(user, key);
		} catch (IOException e) {
			String msg = String.format("[%s] に該当するメッセージは見つかりませんでした", key);
			logger.info(String.format("%s (User:%s)", msg, user), e);
			return ConfigUtil.getSingleMessage(msg);
		}
	}
}
