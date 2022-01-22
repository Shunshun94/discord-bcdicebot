package com.hiyoko.discord.bot.BCDice.AdminCommand;

import java.io.IOException;
import java.util.List;

import org.javacord.api.interaction.SlashCommandInteractionOption;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;

public class RemoveServer implements AdminCommand {
	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client) {
		String url = option.getOptionByIndex(0).get().getStringValue().get();
		try {
			boolean removeResult = client.removeDiceServer(url);
			if(removeResult) {
				return AdminUtil.getSingleMessage(String.format("%s をダイスサーバのリストから削除しました", url));
			} else {
				return AdminUtil.getSingleMessage(String.format("%s をダイスサーバのリストから削除しました", url));
			}			
		} catch (IOException e) {
			return AdminUtil.getSingleMessage(e.getMessage());
		}
	}
}
