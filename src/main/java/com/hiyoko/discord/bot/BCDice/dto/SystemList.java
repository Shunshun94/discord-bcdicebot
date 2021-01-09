package com.hiyoko.discord.bot.BCDice.dto;

import java.util.List;
import java.util.ArrayList;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class SystemList {
	private final List<String> systems;
	
	public SystemList(List<String> list) {
		systems = list;
	}
	
	public SystemList(String json) {
		JsonObject result = Json.parse(json).asObject();
		JsonArray systemListJson = result.get("game_system").asArray();

		List<String> list = new ArrayList<String>();
		for(JsonValue system : systemListJson) {
			list.add(system.asObject().get("id").asString());
		}
		systems = list;
	}
	
	public List<String> getSystemList() {
		return systems;
	}
	public String toString() {
		return "[SystemList] " + systems.size() + " games.";
	}
}
