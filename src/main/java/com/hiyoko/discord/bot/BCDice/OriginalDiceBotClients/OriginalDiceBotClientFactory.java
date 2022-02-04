package com.hiyoko.discord.bot.BCDice.OriginalDiceBotClients;

public class OriginalDiceBotClientFactory {
	private static OriginalDiceBotClient odbc = new OriginalDiceBotClient();
	public static OriginalDiceBotClient getOriginalDiceBotClient() {
		return odbc;
	}
}
