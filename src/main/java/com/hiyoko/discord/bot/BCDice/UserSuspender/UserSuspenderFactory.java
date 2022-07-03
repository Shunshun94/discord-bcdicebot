package com.hiyoko.discord.bot.BCDice.UserSuspender;

public class UserSuspenderFactory {
	private static UserSuspender suspender;
	public static boolean initializeUserSuepnder(String botId) {
		if(suspender == null) {
			suspender = new UserSuspender(botId);
			return true;
		}
		return false;
	}

	public static UserSuspender getUserSuspender() {
		return suspender;
	}
}
