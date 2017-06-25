package com.hiyoko.discord.bot.BCDice.dto;

import org.json.JSONObject;

public class VersionInfo {
	private final String bcdiceVersion;
	private final String apiVersion;
	
	public VersionInfo(String json) {
		JSONObject result = new JSONObject(json);
		
		bcdiceVersion = result.getString("bcdice");
		apiVersion = result.getString("api");
	}
	
	public String getBcDiceVersion() {
		return bcdiceVersion;
	}
	public String getApiVersion() {
		return apiVersion;
	}
	public String toString() {
		return "BCDice: " + bcdiceVersion + " / API: " + apiVersion;
	}
}
