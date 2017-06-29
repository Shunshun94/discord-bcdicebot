package com.hiyoko.discord.bot.BCDice.DiceClient;

import junit.framework.TestCase;

public class DiceClientFactoryTest extends TestCase {

	public void testGetDiceClient() {
		assertTrue(DiceClientFactory.getDiceClient("https://bcdice.herokuapp.com").toString().startsWith("[BCDiceClient]"));
		assertTrue(DiceClientFactory.getDiceClient("test").toString().startsWith("[DiceClientMock]"));
	}

}
