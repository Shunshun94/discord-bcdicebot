package com.hiyoko.discord.bot.BCDice.NameIndicator;

import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.user.User;

public class MentionNameIndicator implements NameIndicator {

	@Override
	public String getName(MessageAuthor user) {
		return String.format("<@%s>", user.getIdAsString());
	}

	@Override
	public String getName(User user) {
		return String.format("<@%s>", user.getIdAsString());
	}

}
