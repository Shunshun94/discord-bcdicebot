package com.hiyoko.discord.bot.BCDice.DiceClient;

import java.util.HashMap;
import java.util.Map;

import com.hiyoko.discord.bot.BCDice.dto.SecretMessage;

public class SavedMessageFactory {
	static Map<String, Map<String, SecretMessage>> savedMessages = new HashMap<String, Map<String, SecretMessage>>();
	public static Map<String, Map<String, SecretMessage>> getSavedMessages() {
		return savedMessages;
	}
	
}
