package com.hiyoko.discord.bot.BCDice.DiceClient;

import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;
import com.hiyoko.discord.bot.BCDice.dto.SystemInfo;
import com.hiyoko.discord.bot.BCDice.dto.SystemList;
import com.hiyoko.discord.bot.BCDice.dto.VersionInfo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * BCDice-API Client
 * @author Shunshun94
 *
 */
public class BCDiceClient implements DiceClient {
	private String url;
	private final Client client;
	private final Map<String, String> system;
	private final boolean errorSensitive;
	private static final String DEFAULT_CHANNEL = "general";

	/**
	 * 
	 * @param bcDiceUrl BCDice-API server URL
	 */
	public BCDiceClient(String bcDiceUrl) {
		url = bcDiceUrl.endsWith("/") ? bcDiceUrl : bcDiceUrl + "/";
		client = ClientBuilder.newBuilder().build();
		system = new HashMap<String, String>();
		system.put(DEFAULT_CHANNEL, "DiceBot");
		errorSensitive = true;
	}
	
	public BCDiceClient(String bcDiceUrl, boolean es) {
		url = bcDiceUrl.endsWith("/") ? bcDiceUrl : bcDiceUrl + "/";
		client = ClientBuilder.newBuilder().build();
		system = new HashMap<String, String>();
		system.put(DEFAULT_CHANNEL, "DiceBot");
		errorSensitive = es;
	}

	/**
	 * 
	 * @param path the path to the called API command
	 * @return the API called result as String
	 * @throws IOException When access is failed
	 */
	private String getUrl(String path) throws IOException {
		Response response = null;
		String targetUrl = url + path;
		try {
			response = client.target(targetUrl).request().get();
		} catch(Exception e) {
			if(response != null) {
				response.close();
			}
			throw new IOException(e.getMessage() + "(" + targetUrl + ")", e);
		}
		
        if (! (response.getStatus() == Response.Status.OK.getStatusCode() || response.getStatus() == 400)) {
        	response.close();
        	if(errorSensitive) {
            	String msg = "[" + response.getStatus() + "] " + targetUrl;
            	throw new IOException(msg);
        	} else {
        		return "{\"ok\":false,\"reason\":\"error handling dummy data\"}";
        	}
        }
        String result = response.readEntity(String.class);
        response.close();
        return result;
	}

	public VersionInfo getVersion() throws IOException {
		return new VersionInfo(getUrl("v1/version"));
	}

	public SystemList getSystems() throws IOException {
		return new SystemList(getUrl("v1/systems"));
	}

	public SystemInfo getSystemInfo(String gameType) throws IOException {
		String rawJson = getUrl("v1/systeminfo?system=" + URLEncoder.encode(gameType, "UTF-8"));
		try {
			// IOException should be thrown from getURL and SystemInfo constructor.
			// I have to show which method throws the Exception.
			return new SystemInfo(rawJson);
		} catch (IOException e) {
			throw new IOException("System '" + gameType + "' is not found", e);
		}
	}


	@Override
	public DicerollResult rollDiceWithChannel(String command, String channel) throws IOException {
		return rollDice(command, getSystem(channel));
	}
	
	public DicerollResult rollDice(String command, String system) throws IOException {
		return new DicerollResult(getUrl("v1/diceroll?command=" + command + "&system=" + URLEncoder.encode(system, "UTF-8").replaceAll("%2520", "%20")));
	}

	public DicerollResult rollDice(String command) throws IOException {
		return rollDice(command, getSystem());
	}

	public String setSystem(String newSystem) {
		return setSystem(newSystem, DEFAULT_CHANNEL);
	}

	@Override
	public String setSystem(String newSystem, String channel) {
		system.put(channel, newSystem);
		return getSystem(channel);
	}
	
	public String getSystem() {
		return getSystem(DEFAULT_CHANNEL);
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
		return "[BCDiceClient] for " + url + " : " + system.get(DEFAULT_CHANNEL);
	}

	public String toString(String channel) {
		return "[BCDiceClient] for " + url + " : " + getSystem(channel);
	}

	@Override
	public void setDiceServer(String bcDiceUrl) {
		url = bcDiceUrl.endsWith("/") ? bcDiceUrl : bcDiceUrl + "/";
	}

	@Override
	public Map<String, String> getRoomsSystem() {
		return system;
	}
}
