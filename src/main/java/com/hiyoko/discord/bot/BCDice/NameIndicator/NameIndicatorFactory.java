package com.hiyoko.discord.bot.BCDice.NameIndicator;

public class NameIndicatorFactory {
	public static NameIndicator getNameIndicator() {
		boolean isMentionMode = (System.getenv("BCDICE_MENTION_MODE") != null);
		if(isMentionMode) {
			return new MentionNameIndicator();
		} else {
			return new SimpleNameIndicator();
		}
	}
}
