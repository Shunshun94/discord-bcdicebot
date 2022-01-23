package com.hiyoko.discord.bot.BCDice.ConfigCommand;

import java.io.IOException;
import java.util.List;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;
import com.hiyoko.discord.bot.BCDice.dto.VersionInfo;

public class Status implements ConfigCommand {
	final Logger logger = LoggerFactory.getLogger(Status.class);
	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client, User user, Channel channel) {
		VersionInfo vi;
		try {
			vi = client.getVersion();
			return ConfigUtil.getSingleMessage(String.format("%s(API v.%s / BCDice v.%s)", client.toString(channel.getIdAsString()), vi.getApiVersion(), vi.getDiceVersion()));
		} catch (IOException e) {
			logger.warn("バージョン情報の取得に失敗しました", e);
			return ConfigUtil.getSingleMessage(String.format("%s(バージョン情報の取得に失敗しました)", client.toString(channel.getIdAsString())));
		}
	}
}
