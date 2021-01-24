package com.hiyoko.discord.bot.BCDice.dto;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OriginalDiceBotTable {
	private static final Pattern ORIGINAL_DICEBOT_VALUE_LINE = Pattern.compile("^\\d+:");
	private static final Pattern DICE_COMMAND_PATTERN = Pattern.compile("\\d*[Dd]\\d+[AaNnSsDd]?");
	private static final Pattern RESULT_VALUE_REGEXP = Pattern.compile("(\\d+)$");
	private static final Logger logger = LoggerFactory.getLogger(OriginalDiceBotTable.class);
	public final boolean isValid;
	private final String body;
	private final String command;
	private final Map<String, String> invalidTableMap;
	private final String name;
	public OriginalDiceBotTable(List<String> fileContents, String name) {
		this.name = name;
		Matcher isDiceCommandMatcher = DICE_COMMAND_PATTERN.matcher(fileContents.get(0).trim());
		boolean isFirstLineCommand = isDiceCommandMatcher.find();
		boolean tmpIsValid = confirmIsValid(fileContents, isFirstLineCommand);
		if(tmpIsValid) {
			if(isFirstLineCommand) {
				this.body = String.format("%s\n%s", name, fileContents.stream().collect(Collectors.joining("\n")));
			} else {
				this.body = fileContents.stream().collect(Collectors.joining("\n"));
			}
			if(this.body.length() > 1000) {
				this.isValid = false;
				logger.warn(String.format("Original table %s is too large(Length:%s)", name, this.body.length()));
			} else {
				this.isValid = true;
			}
		} else {
			String lastLine = fileContents.get(fileContents.size() - 1).trim();
			Matcher isLastLineOldHelpMatcher = ORIGINAL_DICEBOT_VALUE_LINE.matcher(lastLine);
			if(isLastLineOldHelpMatcher.find()) {
				this.body = "";
			} else {
				this.body = lastLine.replaceAll("\\\\n", "\n");
			}
			this.isValid = false;
		}
		if(this.isValid) {
			this.invalidTableMap = null;
			this.command = null;
		} else {
			this.invalidTableMap = getInvalidTableMap(fileContents);
			this.command = isFirstLineCommand ? fileContents.get(0) : fileContents.get(1);
		}
	}

	private Map<String, String> getInvalidTableMap(List<String> fileContents) {
		Map<String, String> result = new HashMap<String, String>();
		fileContents.forEach(line->{
			if(ORIGINAL_DICEBOT_VALUE_LINE.matcher(line).find()) {
				String[] tmp = line.split(":", 2);
				result.put(tmp[0], tmp[1]);
			}
		});
		return result;
	}

	private boolean confirmIsValid(List<String> fileContents, boolean isFirstLineCommand) {
		int startsWith = (isFirstLineCommand) ? 1 : 2;
		for(int i = startsWith; i < fileContents.size(); i++) {
			if(! ORIGINAL_DICEBOT_VALUE_LINE.matcher(fileContents.get(i)).find()) {
				logger.warn(String.format("Original table %s has invalid line [%s] (L#%s)", name, fileContents.get(i), i));
				return false;
			}
		}
		return true;
	}

	public String getResultAsInvalidTable(String result) throws IOException {
		Matcher rollResult = RESULT_VALUE_REGEXP.matcher(result);
		if(rollResult.find()) {
			String diceValue = rollResult.group(1);
			String tableValue = this.invalidTableMap.get(diceValue);
			if(tableValue != null) {
				return String.format("%s(%s) ＞ %s", this.name, diceValue, tableValue.replaceAll("\\\\n", "\n"));
			}
		}
		throw new IOException(String.format("ダイスの結果が取得できませんでした (振った結果:%s / ダイスコマンド:%s)", result, command));
	}

	public String getName() {
		return this.name;
	}

	public String getHelp() {
		return toString();
	}

	public String toString() {
		return this.body; 
	}

	public String getCommand() {
		return command.trim();
	}
}
