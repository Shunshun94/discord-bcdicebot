package com.hiyoko.discord.bot.BCDice.dto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OriginalDiceBotV2 {
	private static final Pattern ORIGINAL_DICEBOT_VALUE_LINE = Pattern.compile("^\\d+[:：]");
	private static final Pattern DICE_COMMAND_PATTERN = Pattern.compile("\\d*[Dd]\\d+[AaNnSsDd]");
	public static final String NO_HELP_MESSAGE = "このダイスボットにはヘルプが登録されていません";
	private final String body;
	public OriginalDiceBotV2(String fileContents, String name) {
		String firstLine = fileContents.split("\\n")[0].trim();
		Matcher isDiceCommand = DICE_COMMAND_PATTERN.matcher(firstLine);
		if(isDiceCommand.find()) {
			this.body = String.format("%s\n%s", name, fileContents);
		} else {
			this.body = fileContents;
		}
	}
	public String getHelp() {
		String lines[] = body.split("\\n");
		String lastLine = lines[lines.length - 1];
		Matcher isNotHelpMatcher = ORIGINAL_DICEBOT_VALUE_LINE.matcher(lastLine);
		if(isNotHelpMatcher.find()) {
			return NO_HELP_MESSAGE;
		} else {
			return lastLine;
		}
	}

	public String toString() {
		return this.body; 
	}
}
