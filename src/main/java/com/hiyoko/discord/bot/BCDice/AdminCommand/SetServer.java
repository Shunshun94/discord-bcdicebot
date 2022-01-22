package com.hiyoko.discord.bot.BCDice.AdminCommand;

import java.io.IOException;
import java.util.List;

import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;
import com.hiyoko.discord.bot.BCDice.dto.VersionInfo;

public class SetServer implements AdminCommand {
	final Logger logger = LoggerFactory.getLogger(SetServer.class);

	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client) {
		try {
			String url = option.getOptionByIndex(0).get().getStringValue().get();
			client.setDiceServer(url);
			VersionInfo vi = client.getVersion();
			String msg = String.format("%s(API v.%s / BCDice v.%s)", client.toString(), vi.getApiVersion(), vi.getDiceVersion()); 
			if(msg.contains(url)) {
				return AdminUtil.getSingleMessage(String.format("%s\nダイスサーバを設定しました", msg));
			} else {
				return AdminUtil.getSingleMessage(String.format("%s\nダイスサーバの設定に失敗しました。上述のサーバの設定を使っています", msg));
			}
		} catch (IOException e) {
			String errorMessage = client.toString() + "(ダイスサーバの情報の取得に失敗しました)";
			logger.warn(errorMessage);
			return AdminUtil.getSingleMessage(errorMessage);
		}
	}
}
