package com.hiyoko.discord.bot.BCDice.ConfigCommand;

import java.util.HashMap;
import java.util.Map;

import com.hiyoko.discord.bot.BCDice.dto.SecretMessage;

public class ConfigCommandsMapFactory {
	public static Map<String, ConfigCommand> getConfigCommands(Map<String, Map<String, SecretMessage>> savedMessages) {
		Map<String, ConfigCommand> configCommands = new HashMap<String, ConfigCommand>();
		configCommands.put("list", new ListDiceSystems());
		configCommands.put("status", new Status());
		configCommands.put("set", new SetDiceSystem());
		configCommands.put("help", new HelpDiceSystem());
		configCommands.put("load", new LoadValue(savedMessages));
		return configCommands;
	}
}
