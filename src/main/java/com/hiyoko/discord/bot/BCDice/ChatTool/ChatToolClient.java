package com.hiyoko.discord.bot.BCDice.ChatTool;

import java.util.List;

public interface ChatToolClient {
	public boolean isRequest(String command);
	public List<String> input(String input);
}
