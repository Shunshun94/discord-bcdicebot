package com.hiyoko.discord.bot.BCDice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;
import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClientFactory;
import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;
import com.hiyoko.discord.bot.BCDice.dto.SystemInfo;
import com.hiyoko.discord.bot.BCDice.dto.VersionInfo;

/**
 * Client for BCDice.
 * The isntance gets the command as String.
 * If it's required, dispatch the command to BCDice.
 * @author @Shunshun94
 *
 */
public class BCDiceCLI {
	private DiceClient client;
	private Map<String, List<String>> savedMessage;

	public static final String HELP = "How to use\n"
			+ "# Show dice bot list\n> bcdice list\n"
			+ "# Change dice bot\n> bcdice set SYSTEM_NAME\n"
			+ "# Show Dice bot help\n> bcdice help SYSTEM_NAME\n"
			+ "# Show current Status\n> bcdice status";
	
	/**
	 * 
	 * @param diceClient Dice Client instance
	 */
	public BCDiceCLI(DiceClient diceClient) {
		client = diceClient;
	}
	
	/**
	 * 
	 * @param diceClient Dice Client instance
	 * @param system BCDice game system
	 */
	public BCDiceCLI(DiceClient diceClient, String system) {
		client = diceClient;
		client.setSystem(system);
	}
	
	/**
	 * @param url BCDice-API URL.
	 */
	public BCDiceCLI(String url) {
		client = DiceClientFactory.getDiceClient(url);
		savedMessage = new HashMap<String, List<String>>();
	}
	
	/**
	 * @param url BCDice-API URL.
	 */
	public BCDiceCLI(String url, boolean errorSenstive) {
		client = DiceClientFactory.getDiceClient(url, errorSenstive);
		savedMessage = new HashMap<String, List<String>>();
	}
	
	/**
	 * 
	 * @param url BCDice-API URL.
	 * @param system BCDice game system
	 */
	public BCDiceCLI(String url, String system) {
		client = DiceClientFactory.getDiceClient(url);
		client.setSystem(system);
		savedMessage = new HashMap<String, List<String>>();
	}
	
	/**
	 * @param inputted command
	 * @return If the command is for roll dice command, true. If not false
	 */
	public boolean isRoll(String input) {
		return ! (input.toLowerCase().startsWith("bcdice ") || input.toLowerCase().equals("bcdice"));
	}
	
	/**
	 * @param input Dice roll command
	 * @return result as DicerollResult instance.
	 * @throws IOException When command failed
	 */
	public DicerollResult roll(String input, String channel) throws IOException {
		return client.rollDiceWithChannel(normalizeDiceCommand(input), channel);
	}
	
	/**
	 * @param input Dice roll command
	 * @return result as DicerollResult instance.
	 * @throws IOException When command failed
	 */
	public DicerollResult roll(String input) throws IOException {
		return client.rollDice(normalizeDiceCommand(input));
	}
	
	/**
	 * @param input command (not dice roll)
	 * @return message from this instance
	 */
	public String input(String input) {
		return input(input, "no_id");
	}

	/**
	 * @param input command (not dice roll)
	 * @param id unique user id
	 * @return message from this instance
	 */
	public String input(String input, String id) {
		return input(input, id, "general");
	}
	/**
	 * 
	 * @param tmpInput (not dice roll)
	 * @param id unique user id
	 * @param channel action target channel
	 * @return message from this instance
	 */
	public String input(String tmpInput, String id, String channel) {
		String input = tmpInput.split("\n")[0];
		String[] command = input.split(" ");
		if(command.length == 1) {
			return HELP;
		}
		if(command[1].equals("help")) {
			if(command.length > 2) {
				try {
					SystemInfo info = client.getSystemInfo(command[2]);
					return "[" + command[2] + "]\n"
							+ info.getInfo();
				} catch (IOException e) {
					return "[" + command[2] + "]\n"
							+ e.getMessage();
				}
			}
		}
		if(command[1].equals("set")) {
			if(command.length > 2) {
				client.setSystem(command[2], channel);
				return "BCDice system is changed: " + command[2];
			} else {
				return "[ERROR] When you want to change dice system\n"
						+ "        bcdice set SYSTEM_NAME\n"
						+ "Example bcdice set AceKillerGene";
			}
			
		}
		if(command[1].equals("list")) {
			StringBuilder sb = new StringBuilder("[DiceBot List]");
			try {
				client.getSystems().getSystemList().forEach(dice->{
					sb.append("\n" + dice);
				});
				return sb.toString();
			} catch (IOException e) {
				return e.getMessage();
			}
		}

		if(command[1].equals("load")) {
			if(command.length == 3) {
				try {
					return getMessage(id, new Integer(command[2]));
				} catch(Exception e) {
					return "Not found (index = " + command[2] + ")";
				}
			}
		}
		
		if(command[1].equals("save")) {
			if(command.length > 2) {
				StringBuilder str = new StringBuilder();
				for(int i = 2; i < command.length; i++) {
					str.append(command[i] + " ");
				} 
				return saveMessage(id, tmpInput.replaceFirst("bcdice save ", "").trim()) + "";
			} else {
				return saveMessage(id, "") + "";
			}
		}
		
		if(command[1].equals("status")) {
			try {
				VersionInfo vi = client.getVersion();
				return client.toString(channel) + "(API v." + vi.getApiVersion() + " / BCDice v." + vi.getDiceVersion() + ")";
			} catch (IOException e) {
				return client.toString(channel) + "(Couldn't get version)";
			}
		}
		
		return HELP;
	}
	
	/**
	 * 
	 * @param tmpInput (not dice roll)
	 * @param id unique user id
	 * @param channel action target channel
	 * @return message from this instance
	 */
	public List<String> inputs(String tmpInput, String id, String channel) {
		List<String> resultList = new ArrayList<String>();
		
		String input = tmpInput.split("\n")[0];
		String[] command = input.split(" ");
		if(command.length == 1) {
			resultList.add(HELP);
			return resultList;
		}
		if(command[1].equals("help")) {
			if(command.length > 2) {
				try {
					SystemInfo info = client.getSystemInfo(command[2]);
					resultList.add("[" + command[2] + "]\n" + info.getInfo());
					return resultList;
				} catch (IOException e) {
					resultList.add("[" + command[2] + "]\n" + e.getMessage());
					return resultList;
				}
			}
		}
		if(command[1].equals("set")) {
			if(command.length > 2) {
				client.setSystem(command[2], channel);
				resultList.add("BCDice system is changed: " + command[2]);
				return resultList;
			} else {
				resultList.add("[ERROR] When you want to change dice system\n"
								+ "        bcdice set SYSTEM_NAME\n"
								+ "Example bcdice set AceKillerGene");
				return resultList;
			}
			
		}
		if(command[1].equals("list")) {
			StringBuilder sb = new StringBuilder("[DiceBot List]");
			try {
				client.getSystems().getSystemList().forEach(dice->{
					sb.append("\n" + dice);
					if(sb.length() > 1000) {
						resultList.add(sb.toString());
						sb.delete(0, sb.length());
					}
				});
				resultList.add(sb.toString());
				return resultList;
			} catch (IOException e) {
				resultList.add(e.getMessage());
				return resultList;
			}
		}

		if(command[1].equals("load")) {
			if(command.length == 3) {
				try {
					resultList.add(getMessage(id, new Integer(command[2])));
					return resultList;
				} catch(Exception e) {
					resultList.add("Not found (index = " + command[2] + ")");
					return resultList;
				}
			}
		}
		
		if(command[1].equals("save")) {
			if(command.length > 2) {
				StringBuilder str = new StringBuilder();
				for(int i = 2; i < command.length; i++) {
					str.append(command[i] + " ");
				}
				resultList.add(saveMessage(id, tmpInput.replaceFirst("bcdice save ", "").trim()) + "");
				return resultList;
			} else {
				resultList.add(saveMessage(id, "") + "");
				return resultList;
			}
		}
		
		if(command[1].equals("status")) {
			try {
				VersionInfo vi = client.getVersion();
				resultList.add(client.toString(channel) + "(API v." + vi.getApiVersion() + " / BCDice v." + vi.getDiceVersion() + ")");
				return resultList;
			} catch (IOException e) {
				resultList.add(client.toString(channel) + "(Couldn't get version)");
				return resultList;
			}
		}
		resultList.add(HELP);
		return resultList;
	}
	
	/**
	 * Stacking secret dice result
	 * @param id user unique id
	 * @param message stacked message
	 * @return The stacked message index
	 */
	private int saveMessage(String id, String message) {
		List<String> msgList = savedMessage.get(id);
		if(msgList == null) {
			msgList = new ArrayList<String>();
			savedMessage.put(id, msgList);
		}
		msgList.add(message);
		return msgList.size();
	}

	/**
	 * 
	 * @param id user unique id
	 * @param index the called message ID
	 * @return the stacked message
	 * @throws IOException When failed to get message
	 */
	private String getMessage(String id, int index) throws IOException {
		try {
			List<String> list = savedMessage.get(id);
			return list.get(index - 1);
		} catch (Exception e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/**
	 * Normalizer for the commands.
	 * See also https://github.com/Shunshun94/discord-bcdicebot/pull/10
	 * @param command raw command
	 * @return Normalized command.
	 * @throws IOException 
	 */
	private String normalizeDiceCommand(String rawCommand) throws IOException {
		String command;
		try {
			command = URLEncoder.encode(rawCommand.replaceAll(" ", "%20"), "UTF-8");
			command = command.replaceAll("%2520", "%20").replaceAll("%7E", "~");
			return command;
		} catch (UnsupportedEncodingException e) {
			throw new IOException("Failed to encode [" + rawCommand + "]", e);
		}
		
	}

	public static void main(String[] args) {

	}

}
