package com.hiyoko.discord.bot.BCDice.UserSuspender;

public class SuspendedUserInfo {
	private final String id;
	private final String reason;
	public SuspendedUserInfo(String id, String reason) {
		this.id = id;
		this.reason = reason;
	}
	public String toString() {
		return String.format("[%s] reason:%s", id, reason);
	}
}
