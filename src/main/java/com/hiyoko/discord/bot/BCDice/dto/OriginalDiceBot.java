package com.hiyoko.discord.bot.BCDice.dto;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OriginalDiceBot {
	private static final Pattern ORIGINAL_DICEBOT_VALUE_LINE = Pattern.compile("^\\d+[:：]");
	public static final String NO_HELP_MESSAGE = "このダイスボットにはヘルプが登録されていません";
	private final String command;
	private final String name;
	private final List<String> body;
	public OriginalDiceBot(List<String> fileContents, String name) {
		this.command = fileContents.get(0);
		this.name = name;
		this.body = fileContents.subList(1, fileContents.size());
	}

	public String getCommand() {
		return command;
	}

	public String getHelp() {
		String lastLine = body.get(body.size() - 1);
		if(lastLine.isEmpty()) {
			return NO_HELP_MESSAGE;
		}
		Matcher isNotHelpMatcher = ORIGINAL_DICEBOT_VALUE_LINE.matcher(lastLine);
		if(isNotHelpMatcher.find()) {
			return NO_HELP_MESSAGE;
		} else {
			return lastLine;
		}
	}

	public String getResult(String result) {
		String prefixA = String.format("%s:", result);
		String prefixB = String.format("%s：", result);
		for(String line : body) {
			if( line.startsWith(prefixA) ) {
				return line.replaceFirst(prefixA, "");
			}
			if( line.startsWith(prefixB) ) {
				return line.replaceFirst(prefixB, "");
			}
		}
		return "結果なし";
	}

	public String getResultAsShow(String result) {
		String rawResult = getResult(result);
		return String.format("(%s[%s]） → \n%s", command, result, rawResult);
	}

	public String toString() {
		return String.format("%s (command: %s)", name, command); 
	}
}
