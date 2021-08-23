package com.hiyoko.discord.bot.BCDice.DiceResultFormatter;

import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;

public class RawDiceResultFormatter implements DiceResultFormatter {
	@Override
	public String getText(DicerollResult dicerollResult) {
		return dicerollResult.getText();
	}

}
