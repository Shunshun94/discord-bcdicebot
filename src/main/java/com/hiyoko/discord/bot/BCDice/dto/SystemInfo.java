package com.hiyoko.discord.bot.BCDice.dto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class SystemInfo {
	private final String name;
	private final String gameType;
	private final List<String> prefixs;
	private final String info;
	
	public SystemInfo(String name, String gameType, List<String> prefixs, String info) {
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
		JsonObject body = result.get("systeminfo").asObject();
		name = body.getString("name", "");
		gameType = body.getString("gameType", "");
		info = body.getString("info", "");

		JsonArray rawPrefixs = body.get("prefixs").asArray();
		List<String> tmpPrefixs = new ArrayList<String>();
		for (JsonValue prefix : rawPrefixs) {
			tmpPrefixs.add(prefix.asString());
		}
		prefixs = tmpPrefixs;
	}
	
	public String getName() {
		return name;
	}
	public String getGameType(){
		return gameType;
	}
	public List<String> getPrefixs(){
		return prefixs;
	}
	public String getInfo(){
		return info;
	}
	public String toString(){
		return getName() + "(" + getGameType() + ")";
	}
}
