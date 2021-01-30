package com.hiyoko.discord.bot.BCDice.NameIndicator;

import org.javacord.api.entity.message.MessageAuthor;

public class MentionNameIndicator implements NameIndicator {

	@Override
	public String getName(MessageAuthor user) {
		return String.format("<@%s>", user.getIdAsString());
	}

}
