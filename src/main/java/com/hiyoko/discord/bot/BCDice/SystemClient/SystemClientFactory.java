package com.hiyoko.discord.bot.BCDice.SystemClient;

public class SystemClientFactory {
	public static SystemClient getSystemClient(String mode) {
		if(mode.equals("mock")) {
			return new NoActionSystemClient();
		}
		return new TextSystemClient();
	}
}
