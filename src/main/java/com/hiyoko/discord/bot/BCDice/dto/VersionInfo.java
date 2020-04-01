package com.hiyoko.discord.bot.BCDice.dto;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

public class VersionInfo {
	private final String diceVersion;
	private final String apiVersion;
	
	public VersionInfo(String dice, String api) {
		diceVersion = dice;
		apiVersion = api;
	}
	
	public VersionInfo(String json) {
		JsonObject result = Json.parse(json).asObject();
		
		diceVersion = result.getString("bcdice", "取得に失敗しました");
		apiVersion = result.getString("api", "取得に失敗しました");
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
