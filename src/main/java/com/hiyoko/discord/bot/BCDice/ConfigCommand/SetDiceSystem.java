package com.hiyoko.discord.bot.BCDice.ConfigCommand;

import java.io.IOException;
import java.util.List;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;
import com.hiyoko.discord.bot.BCDice.SystemClient.SystemClient;
import com.hiyoko.discord.bot.BCDice.SystemClient.SystemClientFactory;

public class SetDiceSystem implements ConfigCommand {
	private final Logger logger = LoggerFactory.getLogger(SetDiceSystem.class);
	private final SystemClient systemClient;
	public SetDiceSystem() {
		systemClient = SystemClientFactory.getSystemClient("");
	}
	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client, User user, Channel channel) {
		String systemName = option.getOptionByIndex(0).get().getStringValue().get();
		return exec(systemName, client, user.getIdAsString(), channel.getIdAsString());
	}

	@Override
	public List<String> exec(String systemName, DiceClient client, String user, String channel) {
		client.setSystem(systemName, channel);
		try {
			systemClient.exportSystemList(client.getRoomsSystem());
		} catch(IOException e) {
			logger.warn("チャンネルごとのダイスボット設定のファイルへの書き出しに失敗しましたが、処理は続行します", e);
		}
		return ConfigUtil.getSingleMessage(String.format("このチャンネルで使うダイスボットのシステムを %s に変更しました", systemName));
	}
}
