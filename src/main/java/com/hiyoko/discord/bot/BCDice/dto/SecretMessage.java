package com.hiyoko.discord.bot.BCDice.dto;

import java.util.Calendar;
import java.util.List;

public class SecretMessage {
	private final List<String> messages;
	private final long limit;
	private final static long THREE_DAYS = 1000 * 60 * 60 * 24 * 3;

	public SecretMessage(List<String> messages, long limit) {
		this.messages = messages;
		this.limit = limit;
	}

	public SecretMessage(List<String> messages) {
		this.messages = messages;
		this.limit = (Calendar.getInstance()).getTimeInMillis() + THREE_DAYS;
	}

	public List<String> getMessages() {
		return this.messages;
	}

	public long getLimit() {
		return this.limit;
	}

	public boolean isDeprecated() {
		return ((Calendar.getInstance()).getTimeInMillis() > this.limit);
	}
}
