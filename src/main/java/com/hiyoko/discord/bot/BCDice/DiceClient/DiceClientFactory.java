package com.hiyoko.discord.bot.BCDice.DiceClient;

public class DiceClientFactory {
	public static DiceClient getDiceClient(String diceSeed) {
		if(diceSeed.startsWith("http")) {
			return new BCDiceClient(diceSeed);
		} else {
			return new DiceClientMock();
		}
	}
}
