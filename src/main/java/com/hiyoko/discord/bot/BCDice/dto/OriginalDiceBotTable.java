package com.hiyoko.discord.bot.BCDice.dto;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OriginalDiceBotTable {
	private static final Pattern ORIGINAL_DICEBOT_VALUE_LINE = Pattern.compile("^\\d+[:：]$");
	private static final Pattern DICE_COMMAND_PATTERN = Pattern.compile("\\d*[Dd]\\d+[AaNnSsDd]");
	public static final String NO_HELP_MESSAGE = "このダイスボットにはヘルプが登録されていません";
	private final String body;
	public OriginalDiceBotTable(List<String> fileContents, String name) {
		System.out.println(String.format("%s:%s lines", name, fileContents.size()));
		Matcher isDiceCommand = DICE_COMMAND_PATTERN.matcher(fileContents.get(0).trim());
		if(isDiceCommand.find()) {
			this.body = String.format("%s\n%s", name, fileContents.stream().collect(Collectors.joining("\n")));
		} else {
			this.body = fileContents.stream().collect(Collectors.joining("\n"));
		}
	}
	public String getHelp() {
		return toString();
	}

	public String toString() {
		return this.body; 
	}
}
