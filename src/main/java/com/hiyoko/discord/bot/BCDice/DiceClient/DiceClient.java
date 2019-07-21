package com.hiyoko.discord.bot.BCDice.DiceClient;

import java.io.IOException;
import java.util.Map;

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
	
	public void setDiceServer(String url);

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
	 * @param channel target channel
	 * @return dice result
	 * @throws IOException When access is failed
	 */
	public DicerollResult rollDiceWithChannel(String command, String channel) throws IOException;

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
	 * change current system.
	 * @param newSystem
	 * @param channel target channel
	 * @return new current system.
	 */
	public String setSystem(String newSystem, String channel);
	
	/**
	 * 
	 * @return current default channel system name
	 */
	public String getSystem();
	
	/**
	 * @param channel target channel
	 * @return current system name
	 */
	public String getSystem(String channel);

	/**
	 * 
	 * @return
	 */
	public Map<String, String> getRoomsSystem();

	/**
	 * 
	 * @param channel target channel
	 * @return
	 */
	public String toString(String channel);
}
