package com.hiyoko.discord.bot.BCDice.DiceClient;

public class DiceClientFactory {
	public static DiceClient getDiceClient(String diceSeed) {
		return getDiceClient(diceSeed, true);
	}
	
	public static DiceClient getDiceClient(String diceSeed, boolean errorSensitive) {
		if(diceSeed.startsWith("http")) {
			return new BCDiceClient(diceSeed, errorSensitive);
		} else {
			return new DiceClientMock();
		}
	}
}
