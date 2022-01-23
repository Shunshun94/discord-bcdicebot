package com.hiyoko.discord.bot.BCDice.ConfigCommand;

import java.util.List;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteractionOption;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;

public interface ConfigCommand {
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client, User user, Channel channel);
}
