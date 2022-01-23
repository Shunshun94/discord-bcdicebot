package com.hiyoko.discord.bot.BCDice.AdminCommand;

import java.util.List;
import java.util.Map;

import org.javacord.api.interaction.SlashCommandInteractionOption;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;
import com.hiyoko.discord.bot.BCDice.DiceClient.SavedMessageFactory;
import com.hiyoko.discord.bot.BCDice.dto.SecretMessage;

public class RefreshSecretDice implements AdminCommand {
	private String refreshSecretMessages() {
		StringBuilder sb = new StringBuilder();
		int i = 0;
		Map<String, Map<String, SecretMessage>> savedMessage = SavedMessageFactory.getSavedMessages();
		for(String userId : savedMessage.keySet()) {
			Map<String, SecretMessage> userMessageMap = savedMessage.get(userId);
			for(String messageId : userMessageMap.keySet()) {
				if( userMessageMap.get(messageId).isDeprecated() ) {
					userMessageMap.remove(messageId);
					sb.append(String.format("削除: User %s / Id %s\n", userId, messageId));
					i++;
				}
			}
		}
		sb.append(String.format("%s件のシークレットダイスの結果を削除しました", i));
		return sb.toString();
	}

	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client) {
		return AdminUtil.getSingleMessage(refreshSecretMessages());
	}
}
