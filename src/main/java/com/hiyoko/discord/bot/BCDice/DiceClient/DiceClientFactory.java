package com.hiyoko.discord.bot.BCDice.DiceClient;

import java.io.IOException;
import java.util.List;

public class DiceClientFactory {
	public static DiceClient getDiceClient(String diceSeed) throws IOException {
		try {
			return getDiceClient(diceSeed, true);
		} catch (IOException e) {
			throw new IOException("ダイスボットの初期化に失敗しました", e);
		}
		
	}

	public static DiceClient getDiceClient(List<String> diceSeeds, boolean errorSensitive) throws IOException {
		boolean isBcDice = true;
		for(String diceSeed : diceSeeds) {
			isBcDice = isBcDice && diceSeed.startsWith("http");
		}
		if(isBcDice) {
			try {
				return new BCDiceV2Client(diceSeeds, errorSensitive);
			}catch (IOException e) {
				throw new IOException("ダイスボットの初期化に失敗しました", e);
			}
		} else {
			return new DiceClientMock();
		}
	}

	public static DiceClient getDiceClient(String diceSeed, boolean errorSensitive) throws IOException {
		if(diceSeed.startsWith("http")) {
			try {
				return new BCDiceV2Client(diceSeed, errorSensitive);
			}catch (IOException e) {
				throw new IOException("ダイスボットの初期化に失敗しました", e);
			}
		} else {
			return new DiceClientMock();
		}
	}
}
