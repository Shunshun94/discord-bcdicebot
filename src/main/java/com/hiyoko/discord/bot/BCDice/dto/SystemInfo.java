package com.hiyoko.discord.bot.BCDice.dto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class SystemInfo {
	private final String name;
	private final String gameType;
	private final List<String> prefixs;
	private final String info;
	public SystemInfo(String json) throws IOException {
		JSONObject result = new JSONObject(json);
		if(! result.getBoolean("ok")) {
			throw new IOException("System not found");
		}
		JSONObject body = result.getJSONObject("systeminfo");
		name = body.getString("name");
		gameType = body.getString("gameType");
		info = body.getString("info");
		
		JSONArray JSONprefixs = body.getJSONArray("prefixs");
		List<String> tmpPrefixs = new ArrayList<String>();
		for(int i = 0; i < JSONprefixs.length(); i++) {
			tmpPrefixs.add(JSONprefixs.getString(i));
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
