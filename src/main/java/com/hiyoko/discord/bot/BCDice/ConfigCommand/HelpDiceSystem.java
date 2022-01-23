package com.hiyoko.discord.bot.BCDice.ConfigCommand;

import java.io.IOException;
import java.util.List;

import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.user.User;
import org.javacord.api.interaction.SlashCommandInteractionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiyoko.discord.bot.BCDice.BCDiceCLI;
import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;
import com.hiyoko.discord.bot.BCDice.OriginalDiceBotClients.OriginalDiceBotClientFactory;
import com.hiyoko.discord.bot.BCDice.dto.OriginalDiceBotTable;
import com.hiyoko.discord.bot.BCDice.dto.SystemInfo;

public class HelpDiceSystem implements ConfigCommand {
	final Logger logger = LoggerFactory.getLogger(HelpDiceSystem.class);
	private final BCDiceCLI cli;
	public HelpDiceSystem(BCDiceCLI cli) {
		this.cli = cli;
	}
	@Override
	public List<String> exec(SlashCommandInteractionOption option, DiceClient client, User user, Channel channel) {
		String systemName = option.getOptionByIndex(0).get().getStringValue().get();
		try {
			String originalDicebot = cli.serachOriginalDicebot(systemName);
			if(originalDicebot.isEmpty() ) {
				SystemInfo info = client.getSystemInfo(systemName);
				return ConfigUtil.getSingleMessage("[" + systemName + "]\n" + info.getInfo());
			} else {
				OriginalDiceBotTable originalDiceBot = OriginalDiceBotClientFactory.getOriginalDiceBotClient().getDiceBot(originalDicebot);
				String helpMessage = originalDiceBot.getHelp();
				return cli.separateStringWithLengthLimitation(helpMessage, 1000);
			}
		} catch (IOException e) {
			logger.warn(String.format("[%s] の情報取得に失敗しました", systemName), e);
			return ConfigUtil.getSingleMessage(String.format("[%s]\n%s", systemName, e.getMessage()));
		}
	}
}
