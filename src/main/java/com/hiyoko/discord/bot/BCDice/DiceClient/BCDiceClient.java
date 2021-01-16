package com.hiyoko.discord.bot.BCDice.DiceClient;

import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;
import com.hiyoko.discord.bot.BCDice.dto.OriginalDiceBotTable;
import com.hiyoko.discord.bot.BCDice.dto.SystemInfo;
import com.hiyoko.discord.bot.BCDice.dto.SystemList;
import com.hiyoko.discord.bot.BCDice.dto.VersionInfo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * BCDice-API Client
 * @author Shunshun94
 *
 */
public class BCDiceClient implements DiceClient {
	private int urlCursor = 0;
	private List<String> urls = new ArrayList<String>();
	private final Client client;
	private final Map<String, String> system;
	private final boolean errorSensitive;
	private static final String DEFAULT_CHANNEL = "general";
	private static final Pattern DICE_COMMAND_PATTERN = Pattern.compile("^[\\w\\+\\-#\\$@<>=\\.\\[\\]\\(\\)]+"); 

	/**
	 * @param bcDiceUrl BCDice-API server URL
	 */
	public BCDiceClient(String bcDiceUrl) {
		urls.add(bcDiceUrl.endsWith("/") ? bcDiceUrl : bcDiceUrl + "/");
		client = ClientBuilder.newBuilder().build();
		system = new HashMap<String, String>();
		system.put(DEFAULT_CHANNEL, "DiceBot");
		errorSensitive = true;
	}

	public BCDiceClient(String bcDiceUrl, boolean es) {
		urls.add(bcDiceUrl.endsWith("/") ? bcDiceUrl : bcDiceUrl + "/");
		client = ClientBuilder.newBuilder().build();
		system = new HashMap<String, String>();
		system.put(DEFAULT_CHANNEL, "DiceBot");
		errorSensitive = es;
	}

	public BCDiceClient(List<String> bcDiceUrls, boolean es) {
		for(String bcDiceUrl : bcDiceUrls) {
			// stream と collect だとあとから追加ができなくなるのでこれで追加
			urls.add(bcDiceUrl.endsWith("/") ? bcDiceUrl : bcDiceUrl + "/");
		}
		client = ClientBuilder.newBuilder().build();
		system = new HashMap<String, String>();
		system.put(DEFAULT_CHANNEL, "DiceBot");
		errorSensitive = es;
	}

	private String getUrl(String path, int rtl) throws IOException {
		Response response = null;
		String targetUrl = urls.get(urlCursor) + path;
		try {
			response = client.target(targetUrl).request().get();
		} catch(Exception e) {
			if(response != null) {
				response.close();
			}
			if( rtl == 0 ) {
				throw new IOException(e.getMessage() + "(" + targetUrl + ")", e);
			} else {
				System.err.println(String.format("Failed to request to %s, %s, app will try %s", targetUrl, e.getMessage(), rtl));
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e.addSuppressed(e1);
					throw new IOException("Waiting in retry is interrupted", e);
				}
				if( urls.size() != 1 ) {
					urlCursor++;
					if(urls.size() <= urlCursor) {
						urlCursor = 0;
					}
				}
				return getUrl(path, rtl - 1);
			}
		}

        if (! (response.getStatus() == Response.Status.OK.getStatusCode() || response.getStatus() == 400)) {
        	response.close();
        	if(errorSensitive) {
            	String msg = String.format("[%s] %s", response.getStatus(), targetUrl);
            	if(msg.startsWith("[5") && (urls.size() != 1) && (rtl > 0)) { // 5XX Error であれば かつ 予備 URL があれば
            		urlCursor++;
            		if(urls.size() <= urlCursor) {
            			urlCursor = 0;
            		}
            		System.err.println(String.format("Failed to request to %s, %s, app will try %s with dice server %s",
            				targetUrl,
            				response.getStatus(),
            				rtl,
            				urls.get(urlCursor)));
            		return getUrl(path, rtl - 1);
            	}
            	throw new IOException(msg);
        	} else {
        		return "{\"ok\":false,\"reason\":\"error handling dummy data\"}";
        	}
        }
        String result = response.readEntity(String.class);
        response.close();
        return result;
	}

	/**
	 * 
	 * @param path the path to the called API command
	 * @return the API called result as String
	 * @throws IOException When access is failed
	 */
	private String getUrl(String path) throws IOException {
		return getUrl(path, 5);
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
		return new DicerollResult(getUrl("v1/diceroll?command=" + command + "&system=" + URLEncoder.encode(system, "UTF-8").replaceAll("%2520", "%20")), system);
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
		return "[BCDiceClient] for " + urls.get(urlCursor) + " : " + system.get(DEFAULT_CHANNEL);
	}

	public String toString(String channel) {
		return "[BCDiceClient] for " + urls.get(urlCursor) + " : " + getSystem(channel);
	}

	@Override
	public void setDiceServer(String bcDiceUrl) {
		String tmp = bcDiceUrl.endsWith("/") ? bcDiceUrl : bcDiceUrl + "/";
		for(int i = 0; i < urls.size(); i++) {
			if(urls.get(i).equals(tmp) ) {
				urlCursor = i;
				return;
			}
		}
		urls.add(tmp);
		urlCursor = urls.size() - 1;
	}

	@Override
	public Map<String, String> getRoomsSystem() {
		return system;
	}

	@Override
	public boolean isDiceCommand(String command) {
		if(command.startsWith("choice[")) {return true;}
		if(command.startsWith("http")) {return false;}
		return DICE_COMMAND_PATTERN.matcher(command).find();
	}

	public int getUrlCursor() {
		return urlCursor;
	}

	public List<String> getDiceUrlList() {
		return urls;
	}

	public boolean removeDiceServer(String bcDiceUrl) throws IOException {
		if(urls.size() == 1) {
			throw new IOException(
					String.format("今登録されているダイスサーバ %s を削除したらダイスサーバが無くなるため、ダイスサーバの削除ができません",
							urls.get(0)));
		}
		String tmp = bcDiceUrl.endsWith("/") ? bcDiceUrl : bcDiceUrl + "/";
		boolean flag = false;
		List<String> newUrlList = new ArrayList<String>();
		for(int i = 0; i < urls.size(); i++) {
			String currentUrl = urls.get(i);
			if( ! currentUrl.equals(tmp) ) {
				newUrlList.add(currentUrl);
			} else {
				flag = true;
			}
		}
		urls = newUrlList;
		if(urls.size() <= urlCursor) {
			urlCursor = 0;
		}
		return flag;
	}

	@Override
	public DicerollResult rollOriginalDiceBotTable(OriginalDiceBotTable diceBot) throws IOException {
		throw new IOException(
				"rollOriginalDiceBotTable はこのバージョンでは使えません",
				new UnsupportedOperationException("API V1 はオリジナルダイスボット表をサポートしていません"));
	}
}
