package com.hiyoko.discord.bot.BCDice.ChatTool;

import junit.framework.TestCase;

public class DiscordClientTest extends TestCase {

	public void testGetDiceClient() {
		DiscordClient dc = new DiscordClient(null);
		assertEquals("\\*\\*", dc.formatMessage("**"));
		assertEquals("\\*\\*てすとてすと\\*\\*", dc.formatMessage("**てすとてすと**"));
		assertEquals("\\*てすとてすと\\*", dc.formatMessage("*てすとてすと*"));
		
	}

}
