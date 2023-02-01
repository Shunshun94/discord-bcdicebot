package com.hiyoko.discord.bot.BCDice.DiceClient;

import java.io.IOException;

import junit.framework.TestCase;

public class DiceClientFactoryTest extends TestCase {

	public void testGetDiceClient() throws IOException {
		assertTrue(DiceClientFactory.getDiceClient("https://bcdice.onlinesession.app").toString().startsWith("[BCDiceClient]"));
		assertTrue(DiceClientFactory.getDiceClient("test").toString().startsWith("[DiceClientMock]"));
	}

}
