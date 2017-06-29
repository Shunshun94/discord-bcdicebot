package com.hiyoko.discord.bot.BCDice.dto;

import org.json.JSONObject;

public class DicerollResult {
	private final String text;
	private final boolean secret;
	private final boolean rolled;
	private final String system;
	
	public DicerollResult(String text, String system, boolean secret, boolean rolled) {
		this.text = text;
		this.secret = secret;
		this.rolled = rolled;
		this.system = system;
	}
	
	public DicerollResult(String json, String usedSystem) {
		JSONObject result = new JSONObject(json);
		rolled = result.getBoolean("ok");
		if(rolled) {
			system = usedSystem;
			text = result.getString("result");
			secret = result.getBoolean("secret");
		} else {
			system = "";
			text = "";
			secret = false;
		}
	}
	
	public DicerollResult(String json) {
		JSONObject result = new JSONObject(json);
		rolled = result.getBoolean("ok");
		if(rolled) {
			system = "DiceBot";
			text = result.getString("result");
			secret = result.getBoolean("secret");
		} else {
			system = "";
			text = "";
			secret = false;
		}
	}
	
	public String getText() {
		return text;
	}
	
	public String getSystem() {
		return system;
	}
	
	public boolean isSecret() {
		return secret;
	}
	
	public boolean isRolled() {
		return rolled;
	}
	
	public String toString() {
		if(secret) {
			return system + ": [Secret Dice]";
		} else {
			return system + text;
		}
	}
}
