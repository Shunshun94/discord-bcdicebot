package com.hiyoko.discord.bot.BCDice.DiceClient;

import java.io.IOException;

import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;
import com.hiyoko.discord.bot.BCDice.dto.SystemInfo;
import com.hiyoko.discord.bot.BCDice.dto.SystemList;
import com.hiyoko.discord.bot.BCDice.dto.VersionInfo;

public interface DiceClient {
	/**
	 * 
	 * @return version info of the server
	 * @throws IOException When access is failed
	 */
	public VersionInfo getVersion() throws IOException;
	
	/**
	 * 
	 * @return The dice systems list
	 * @throws IOException When access is failed
	 */
	public SystemList getSystems() throws IOException;
	
	/**
	 * 
	 * @param gameType game name
	 * @return The detail of the dice system of the rule
	 * @throws IOException When access is failed
	 */
	public SystemInfo getSystemInfo(String gameType) throws IOException;
	
	/**
	 * 
	 * @param command dice command
	 * @param system dice system of the rule
	 * @return dice result
	 * @throws IOException When access is failed
	 */
	public DicerollResult rollDice(String command, String system) throws IOException;
	
	/**
	 * rollDice method with the current system.
	 * @param command dice command
	 * @return dice result
	 * @throws IOException
	 */
	public DicerollResult rollDice(String command) throws IOException;
	
	/**
	 * change current system.
	 * @param newSystem
	 * @return new current system.
	 */
	public String setSystem(String newSystem);
	/**
	 * 
	 * @return current system name
	 */
	public String getSystem();
}
