package com.hiyoko.discord.bot.BCDice.NameIndicator;

import org.javacord.api.entity.message.MessageAuthor;

public class SimpleNameIndicator implements NameIndicator {

	@Override
	public String getName(MessageAuthor user) {
		return user.getName();
	}

}
