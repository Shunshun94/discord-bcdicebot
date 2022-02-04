package com.hiyoko.discord.bot.BCDice.NameIndicator;

import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.user.User;

public interface NameIndicator {
	public String getName(MessageAuthor user);
	public String getName(User user);
}
