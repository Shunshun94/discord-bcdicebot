package com.hiyoko.discord.bot.BCDice.DiceClient;

import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;
import com.hiyoko.discord.bot.BCDice.dto.OriginalDiceBotTable;
import com.hiyoko.discord.bot.BCDice.dto.SystemInfo;
import com.hiyoko.discord.bot.BCDice.dto.SystemList;
import com.hiyoko.discord.bot.BCDice.dto.VersionInfo;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * BCDice-API Client
 * 
 * @author Shunshun94
 *
 */
public class BCDiceV2Client implements DiceClient {
	private int urlCursor = 0;
	private List<String> urls = new ArrayList<String>();
	private final OkHttpClient client;
	private final Map<String, String> system;
	private final boolean errorSensitive;
	private static final String DEFAULT_CHANNEL = "general";
	private static final Pattern DICE_COMMAND_PATTERN = Pattern.compile("^[\\w\\+\\-#\\$@<>=\\.\\[\\]\\(\\)]+");
	
	/**
	 * @param bcDiceUrl BCDice-API server URL
	 */
	public BCDiceV2Client(String bcDiceUrl) {
		client = new OkHttpClient();
		urls.add(bcDiceUrl.endsWith("/") ? bcDiceUrl : bcDiceUrl + "/");
		system = new HashMap<String, String>();
		system.put(DEFAULT_CHANNEL, "DiceBot");
		errorSensitive = true;
	}

	public BCDiceV2Client(String bcDiceUrl, boolean es) {
		urls.add(bcDiceUrl.endsWith("/") ? bcDiceUrl : bcDiceUrl + "/");
		client = new OkHttpClient();
		system = new HashMap<String, String>();
		system.put(DEFAULT_CHANNEL, "DiceBot");
		errorSensitive = es;
	}

	public BCDiceV2Client(List<String> bcDiceUrls, boolean es) {
		for (String bcDiceUrl : bcDiceUrls) {
			// stream と collect だとあとから追加ができなくなるのでこれで追加
			urls.add(bcDiceUrl.endsWith("/") ? bcDiceUrl : bcDiceUrl + "/");
		}
		client = new OkHttpClient();
		system = new HashMap<String, String>();
		system.put(DEFAULT_CHANNEL, "DiceBot");
		errorSensitive = es;
	}

	private String postUrl(String path, int rtl) throws IOException {
		String targetUrl = urls.get(urlCursor) + path;
		Request request = new Request.Builder().url(targetUrl).post( new FormBody.Builder().build() ).build();
		try (Response response = client.newCall(request).execute();) {
			ResponseBody result = response.body();
			int responseCode = response.code(); 
			if (responseCode == DiceClientConsts.REQUEST_URI_TOO_LONG ) {
				throw new IOException("Too long command is requested. Make the command shorter");
			}
			if (!(responseCode == DiceClientConsts.OK || responseCode == DiceClientConsts.BAD_REQUEST)) {
				response.close();
				if (errorSensitive) {
					String msg = String.format("[%s] %s", responseCode, targetUrl);
					if (msg.startsWith("[5") && (urls.size() != 1) && (rtl > 0)) { // 5XX Error であれば かつ 予備 URL があれば
						urlCursor++;
						if (urls.size() <= urlCursor) {
							urlCursor = 0;
						}
						System.err.println(String.format("Failed to request to %s, %s, app will try %s with dice server %s",
								targetUrl, responseCode, rtl, urls.get(urlCursor)));
						return postUrl(path, rtl - 1);
					}
					throw new IOException(msg);
				} else {
					return "{\"ok\":false,\"reason\":\"error handling dummy data\"}";
				}
			}
			return result.string();
		} catch (Exception e) {
			if (rtl == 0) {
				throw new IOException(e.getMessage() + "(" + targetUrl + ")", e);
			} else {
				System.err.println(
						String.format("Failed to request to %s, %s, app will try %s", targetUrl, e.getMessage(), rtl));
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e.addSuppressed(e1);
					throw new IOException("Waiting in retry is interrupted", e);
				}
				if (urls.size() != 1) {
					urlCursor++;
					if (urls.size() <= urlCursor) {
						urlCursor = 0;
					}
				}
				return postUrl(path, rtl - 1);
			}
		}
	}

	private String getUrl(String path, int rtl) throws IOException {
		
		String targetUrl = urls.get(urlCursor) + path;
		Request request = new Request.Builder().url(targetUrl).build();
		try (Response response = client.newCall(request).execute();) {
			ResponseBody result = response.body();
			int responseCode = response.code(); 
			if (!(responseCode == DiceClientConsts.OK || responseCode == DiceClientConsts.BAD_REQUEST)) {
				response.close();
				if (errorSensitive) {
					String msg = String.format("[%s] %s", responseCode, targetUrl);
					if (msg.startsWith("[5") && (urls.size() != 1) && (rtl > 0)) { // 5XX Error であれば かつ 予備 URL があれば
						urlCursor++;
						if (urls.size() <= urlCursor) {
							urlCursor = 0;
						}
						System.err.println(String.format("Failed to request to %s, %s, app will try %s with dice server %s",
								targetUrl, responseCode, rtl, urls.get(urlCursor)));
						return getUrl(path, rtl - 1);
					}
					throw new IOException(msg);
				} else {
					return "{\"ok\":false,\"reason\":\"error handling dummy data\"}";
				}
			}
			return result.string();
		} catch (Exception e) {
			if (rtl == 0) {
				throw new IOException(e.getMessage() + "(" + targetUrl + ")", e);
			} else {
				System.err.println(
						String.format("Failed to request to %s, %s, app will try %s", targetUrl, e.getMessage(), rtl));
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e.addSuppressed(e1);
					throw new IOException("Waiting in retry is interrupted", e);
				}
				if (urls.size() != 1) {
					urlCursor++;
					if (urls.size() <= urlCursor) {
						urlCursor = 0;
					}
				}
				return getUrl(path, rtl - 1);
			}
		}
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
		return new VersionInfo(getUrl("v2/version"));
	}

	public SystemList getSystems() throws IOException {
		return new SystemList(getUrl("v2/game_system"));
	}

	public SystemInfo getSystemInfo(String gameType) throws IOException {
		String rawJson = getUrl("v2/game_system/" + URLEncoder.encode(gameType, "UTF-8").replaceAll("%2520", "%20"));
		try {
			// IOException should be thrown from getURL and SystemInfo constructor.
			// I have to show which method throws the Exception.
			return new SystemInfo(rawJson);
		} catch (IOException e) {
			throw new IOException("System '" + gameType + "' is not found", e);
		}
	}

	@Override
	public DicerollResult rollOriginalDiceBotTable(OriginalDiceBotTable diceBot) throws IOException {
		String result = postUrl(
				"v2/original_table?table=" + URLEncoder.encode(diceBot.toString(), "UTF-8").replaceAll("%2520", "%20"),
				5);
		return new DicerollResult(result);
	}

	@Override
	public DicerollResult rollDiceWithChannel(String command, String channel) throws IOException {
		return rollDice(command, getSystem(channel));
	}

	public DicerollResult rollDice(String command, String system) throws IOException {
		return new DicerollResult(getUrl("v2/game_system/"
				+ URLEncoder.encode(system, "UTF-8").replaceAll("%2520", "%20") + "/roll?command=" + command), system);
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
		if (channelSystem != null) {
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
		for (int i = 0; i < urls.size(); i++) {
			if (urls.get(i).equals(tmp)) {
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
		if (command.startsWith("choice[")) {
			return true;
		}
		if (command.startsWith("http")) {
			return false;
		}
		return DICE_COMMAND_PATTERN.matcher(command).find();
	}

	public int getUrlCursor() {
		return urlCursor;
	}

	public List<String> getDiceUrlList() {
		return urls;
	}

	public boolean removeDiceServer(String bcDiceUrl) throws IOException {
		if (urls.size() == 1) {
			throw new IOException(String.format("今登録されているダイスサーバ %s を削除したらダイスサーバが無くなるため、ダイスサーバの削除ができません", urls.get(0)));
		}
		String tmp = bcDiceUrl.endsWith("/") ? bcDiceUrl : bcDiceUrl + "/";
		boolean flag = false;
		List<String> newUrlList = new ArrayList<String>();
		for (int i = 0; i < urls.size(); i++) {
			String currentUrl = urls.get(i);
			if (!currentUrl.equals(tmp)) {
				newUrlList.add(currentUrl);
			} else {
				flag = true;
			}
		}
		urls = newUrlList;
		if (urls.size() <= urlCursor) {
			urlCursor = 0;
		}
		return flag;
	}
}
