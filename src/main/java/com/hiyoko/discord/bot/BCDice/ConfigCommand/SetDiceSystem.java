package com.hiyoko.discord.bot.BCDice.ConfigCommand;

import java.util.List;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;

public class SetDiceSystem implements ConfigCommand {

	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client, User user, Channel channel) {
		String systemName = option.getOptionByIndex(0).get().getStringValue().get();
		return exec(systemName, client, user.getIdAsString(), channel.getIdAsString());
	}

	@Override
	public List<String> exec(String systemName, DiceClient client, String user, String channel) {
		client.setSystem(systemName, channel);
		return ConfigUtil.getSingleMessage(String.format("このチャンネルで使うダイスボットのシステムを %s に変更しました", systemName));
	}
}
