package com.hiyoko.discord.bot.BCDice.NameIndicator;

import org.javacord.api.entity.message.MessageAuthor;

public interface NameIndicator {
	public String getName(MessageAuthor user);
}
