package com.hiyoko.discord.bot.BCDice.dto;

import java.io.IOException;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

public class SystemInfo {
	private final String name;
	private final String gameType;
	private final String prefixs;
	private final String info;
	
	public SystemInfo(String name, String gameType, String prefixs, String info) {
		this.name = name;
		this.gameType = gameType;
		this.prefixs = prefixs;
		this.info = info;
	}
	
	public SystemInfo(String json) throws IOException {
		JsonObject result = Json.parse(json).asObject();
		if(! result.getBoolean("ok", false)) {
			throw new IOException("System not found");
		}
		name = result.getString("name", "");
		gameType = result.getString("id", "");
		info = result.getString("help_message", "");
		prefixs = result.getString("command_pattern", "");
	}
	
	public String getName() {
		return name;
	}
	public String getGameType(){
		return gameType;
	}
	public String getPrefixs(){
		return prefixs;
	}
	public String getInfo(){
		return info;
	}
	public String toString(){
		return getName() + "(" + getGameType() + ")";
	}
}
