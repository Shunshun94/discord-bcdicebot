package com.hiyoko.discord.bot.BCDice.dto;

import java.util.List;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class SystemList {
	private final List<String> systems;
	
	public SystemList(List<String> list) {
		systems = list;
	}
	
	public SystemList(String json) {
		JSONObject result = new JSONObject(json);
		JSONArray systemListJson = result.getJSONArray("systems");
		
		List<String> list = new ArrayList<String>();
		
		
		int length = systemListJson.length();
		for(int i = 0; i < length; i++) {
			list.add(systemListJson.getString(i));
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
