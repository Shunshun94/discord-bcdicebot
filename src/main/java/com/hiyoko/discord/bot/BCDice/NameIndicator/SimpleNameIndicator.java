package com.hiyoko.discord.bot.BCDice.NameIndicator;

import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.user.User;

public class SimpleNameIndicator implements NameIndicator {

	@Override
	public String getName(MessageAuthor user) {
		return user.getName();
	}

	@Override
	public String getName(User user) {
		return user.getName();
	}

}
