package com.hiyoko.discord.bot.BCDice.dto;

import java.util.List;

public class OriginalDiceBot {
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

	public String getResult(String result) {
		String prefixA = String.format("%s:", result);
		String prefixB = String.format("%s：", result);
		for(String line : body) {
			if( line.startsWith(prefixA) ) {
				return line.replace(prefixA, "");
			}
			if( line.startsWith(prefixB) ) {
				return line.replace(prefixB, "");
			}
		}
		return "結果なし";
	}

	public String getResultAsShow(String result) {
		String rawResult = getResult(result);
		return String.format(": (%s[%s]） → \n%s", command, result, rawResult);
	}

	public String toString() {
		return String.format("%s (command: %s)", name, command); 
	}
}
