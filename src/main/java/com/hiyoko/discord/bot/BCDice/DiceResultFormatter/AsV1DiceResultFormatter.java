package com.hiyoko.discord.bot.BCDice.DiceResultFormatter;

import java.util.Arrays;
import java.util.List;

import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;

public class AsV1DiceResultFormatter implements DiceResultFormatter {

	@Override
	public String getText(DicerollResult dicerollResult) {
		String rawText = dicerollResult.toString();
		if(! rawText.startsWith("#")) {
			return String.format("%s: %s", dicerollResult.getSystem(), rawText);
		}
		List<String> separatedRawResult = Arrays.asList(rawText.split("\\n"));
		StringBuilder sb = new StringBuilder();
		boolean flag = false;
		String system = dicerollResult.getSystem();
		for( String line : separatedRawResult ) {
			if(line.startsWith("#")) {
				flag = true;
				sb.append(String.format("%s\n", line));
			} else {
				if(flag && (! line.isEmpty())) {
					sb.append(String.format("%s: %s\n", system, line));
				} else {
					sb.append(String.format("%s\n", line));
				}
				flag = false;
			}
		}
		return sb.toString().trim();
	}
}
