package com.hiyoko.discord.bot.BCDice.DiceClient;

import java.io.IOException;
import java.util.Arrays;

import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;
import com.hiyoko.discord.bot.BCDice.dto.SystemInfo;
import com.hiyoko.discord.bot.BCDice.dto.SystemList;
import com.hiyoko.discord.bot.BCDice.dto.VersionInfo;

public class DiceClientMock implements DiceClient {
	private String system = "Hiyoko";
	private final String[] systemList = {"Hiyoko", "Hitsuji", "Koneko"};
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
		return rollDice(command, system);
	}

	@Override
	public String setSystem(String newSystem) {
		system = newSystem;
		return system;
	}

	@Override
	public String getSystem() {
		return system;
	}
	
	public String toString() {
		return "[DiceClientMock]";
	}

}
