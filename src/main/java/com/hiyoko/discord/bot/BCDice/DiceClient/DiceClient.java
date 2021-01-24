package com.hiyoko.discord.bot.BCDice.DiceClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;
import com.hiyoko.discord.bot.BCDice.dto.OriginalDiceBotTable;
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
	 * Changing dice server
	 * @param url 
	 */
	public void setDiceServer(String url);
	
	/**
	 * 
	 * @return dice server url list
	 */
	public List<String> getDiceUrlList();

	/**
	 * 
	 * @param url
	 * @throws IOException Remove is not acceptable
	 */
	public boolean removeDiceServer(String url) throws IOException;
	
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
	 * 
	 * @param diceBot
	 * @return Result of Original DiceBot Table
	 * @throws IOException
	 */
	public DicerollResult rollOriginalDiceBotTable(OriginalDiceBotTable diceBot) throws IOException;

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
	 * @param command
	 * @return
	 */
	public boolean isDiceCommand(String command);

	/**
	 * 
	 * @param channel target channel
	 * @return
	 */
	public String toString(String channel);
}
