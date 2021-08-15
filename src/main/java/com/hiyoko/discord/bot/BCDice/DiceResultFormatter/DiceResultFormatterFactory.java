package com.hiyoko.discord.bot.BCDice.DiceResultFormatter;

import java.util.Arrays;

public class DiceResultFormatterFactory {
	private static final String[] V1_VALUES = {"v1", "V1"};
	
	public static DiceResultFormatter getDiceResultFormatter() {
		String BCDICE_RESULT_DISPLAY_FORMAT = System.getenv("BCDICE_RESULT_DISPLAY_FORMAT");
		if(BCDICE_RESULT_DISPLAY_FORMAT == null) {
			return new RawDiceResultFormatter();
		}
		if(Arrays.asList(V1_VALUES).contains(BCDICE_RESULT_DISPLAY_FORMAT)) {
			return new AsV1DiceResultFormatter();
		}
		return new RawDiceResultFormatter();
	}
}
