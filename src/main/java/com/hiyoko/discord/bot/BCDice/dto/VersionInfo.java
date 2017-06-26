package com.hiyoko.discord.bot.BCDice.dto;

import org.json.JSONObject;

public class VersionInfo {
	private final String diceVersion;
	private final String apiVersion;
	
	public VersionInfo(String json) {
		JSONObject result = new JSONObject(json);
		
		diceVersion = result.getString("bcdice");
		apiVersion = result.getString("api");
	}
	
	public String getDiceVersion() {
		return diceVersion;
	}
	public String getApiVersion() {
		return apiVersion;
	}
	public String toString() {
		return "BCDice: " + diceVersion + " / API: " + apiVersion;
	}
}
