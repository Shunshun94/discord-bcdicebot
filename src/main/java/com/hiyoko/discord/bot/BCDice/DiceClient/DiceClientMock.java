package com.hiyoko.discord.bot.BCDice.DiceClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;
import com.hiyoko.discord.bot.BCDice.dto.SystemInfo;
import com.hiyoko.discord.bot.BCDice.dto.SystemList;
import com.hiyoko.discord.bot.BCDice.dto.VersionInfo;

public class DiceClientMock implements DiceClient {
	private final String[] systemList = {"Hiyoko", "Hitsuji", "Koneko"};
	private final Map<String, String> system = new HashMap<String, String>();
	private static final String DEFAULT_CHANNEL = "general";
	private static final Pattern DICE_COMMAND_PATTERN = Pattern.compile("^S?\\d+d\\d+"); 

	public DiceClientMock() {
		system.put(DEFAULT_CHANNEL, "Hiyoko");
	}
	
	@Override
	public VersionInfo getVersion() throws IOException {
		return new VersionInfo("hiyoko", "hitsuji");
	}

	@Override
	public SystemList getSystems() throws IOException {
		return new SystemList(Arrays.asList(systemList));
	}

	@Override
	public SystemInfo getSystemInfo(String gameType) throws IOException {
		if(Arrays.asList(systemList).contains(gameType)) {
			String[] prefix = {};
			return new SystemInfo(gameType, gameType + "-game", Arrays.asList(prefix), gameType + " sample dice bot system.");			
		}
		throw new IOException("System '" + gameType + "' is not found");
	}

	@Override
	public DicerollResult rollDice(String command, String system) throws IOException {
		if(command.equals("2d6")) {
			return new DicerollResult("(2D6) ＞ 6[4,2] ＞ 6", system, false, true);
		}
		if(command.equals("S2d6")) {
			return new DicerollResult("(2D6) ＞ 6[4,2] ＞ 6", system, true, true);
		}
		return new DicerollResult(command, system, true, true);
	}

	@Override
	public DicerollResult rollDice(String command) throws IOException {
		return rollDice(command, system.get(DEFAULT_CHANNEL));
	}

	@Override
	public String setSystem(String newSystem) {
		return setSystem(newSystem, DEFAULT_CHANNEL);
	}


	@Override
	public String setSystem(String newSystem, String channel) {
		system.put(channel, newSystem);
		return system.get(channel);
	}
	
	@Override
	public String getSystem() {
		return system.get(DEFAULT_CHANNEL);
	}

	@Override
	public String getSystem(String channel) {
		String channelSystem = system.get(channel);
		if(channelSystem != null) {
			return channelSystem;
		}
		return system.get(DEFAULT_CHANNEL);
	}
	
	public String toString() {
		return "[DiceClientMock] for Mock : " + system.get(DEFAULT_CHANNEL);
	}

	@Override
	public DicerollResult rollDiceWithChannel(String command, String channel) throws IOException {
		return rollDice(command, getSystem(channel));
	}

	@Override
	public String toString(String channel) {
		return "[DiceClientMock] for Mock : " + getSystem(channel);
	}

	@Override
	public void setDiceServer(String url) {
		//
	}

	@Override
	public Map<String, String> getRoomsSystem() {
		return system;
	}

	@Override
	public boolean isDiceCommand(String command) {
		return DICE_COMMAND_PATTERN.matcher(command).find();
	}
}
