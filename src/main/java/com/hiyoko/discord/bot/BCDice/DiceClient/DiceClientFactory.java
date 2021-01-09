package com.hiyoko.discord.bot.BCDice.DiceClient;

import java.util.List;

public class DiceClientFactory {
	public static DiceClient getDiceClient(String diceSeed) {
		return getDiceClient(diceSeed, true);
	}

	public static DiceClient getDiceClient(List<String> diceSeeds, boolean errorSensitive) {
		boolean isBcDice = true;
		for(String diceSeed : diceSeeds) {
			isBcDice = isBcDice && diceSeed.startsWith("http");
		}
		if(isBcDice) {
			return new BCDiceV2Client(diceSeeds, errorSensitive);
		} else {
			return new DiceClientMock();
		}
	}

	public static DiceClient getDiceClient(String diceSeed, boolean errorSensitive) {
		if(diceSeed.startsWith("http")) {
			return new BCDiceV2Client(diceSeed, errorSensitive);
		} else {
			return new DiceClientMock();
		}
	}
}
