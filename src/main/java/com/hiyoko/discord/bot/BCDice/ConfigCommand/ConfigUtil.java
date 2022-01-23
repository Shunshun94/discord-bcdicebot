package com.hiyoko.discord.bot.BCDice.ConfigCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigUtil {
	public static List<String> separateStringWithLengthLimitation(String raw, int limitLength) {
		return separateStringWithLengthLimitation(Arrays.asList(raw.split("\\n")), limitLength);
	}

	public static List<String> separateStringWithLengthLimitation(List<String> raw, int limitLength) {
		List<String> result = new ArrayList<String>();
		StringBuilder sb = new StringBuilder("");
		raw.forEach(line->{
			sb.append(line + "\n");
			if(sb.length() > limitLength) {
				result.add(sb.toString());
				sb.delete(0, sb.length());
			}
		});
		result.add(sb.toString());
		return result;
	}

	 public static List<String> getSingleMessage(String message) {
		List<String> msg = new ArrayList<String>();
		msg.add(message);
		return msg;
	}
}
