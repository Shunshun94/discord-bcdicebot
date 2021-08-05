package com.hiyoko.discord.bot.BCDice.DiceResultFormatter;

import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;

public interface DiceResultFormatter {
	public String getText(DicerollResult dicerollResult);
}
