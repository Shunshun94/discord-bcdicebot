package com.hiyoko.discord.bot.BCDice.SystemClient;

import java.io.IOException;
import java.util.Map;

public interface SystemClient {
	public int exportSystemList(Map<String, String> systems) throws IOException;
	public Map<String, String> getSystemList() throws IOException;
}
