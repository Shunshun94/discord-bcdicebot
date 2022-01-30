package com.hiyoko.discord.bot.BCDice.SystemClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NoActionSystemClient implements SystemClient {

	@Override
	public int exportSystemList(Map<String, String> systems) throws IOException {
		return 0;
	}

	@Override
	public Map<String, String> getSystemList() throws IOException {
		return new HashMap<String, String>();
	}
}
