package com.hiyoko.discord.bot.BCDice.ChatTool;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.javacord.api.DiscordApi;

public class DiscordClient implements ChatToolClient {
	private final DiscordClientV2 client;
	private final String password;
	private final String HELP_MESSAGE = "bcdicediscord コマンドの使い方\n"
			+ "# チャンネルの ID を一覧する\n"
			+ "> bcdicediscord PASSWORD listRoomIds\n"
			+ "# チャンネルを一覧する\n"
			+ "> bcdicediscord PASSWORD listRooms\n"
			+ "# サーバを一覧する\n"
			+ "> bcdicediscord PASSWORD listServers";

	public DiscordClient(DiscordApi api, String password) {
		this.client = new DiscordClientV2(api);
		this.password = password;
	}

	public DiscordClient(DiscordApi api) {
		this.client = new DiscordClientV2(api);
		this.password = getPassword();
	}

	public boolean isRequest(String command) {
		return command.startsWith("bcdicediscord ");
	}

	public List<String> input(String input) {
		List<String> result = new ArrayList<String>();
		String[] inputArray = input.split(" ");
		if(inputArray.length > 2 && inputArray[1].equals(password)) {
			if(inputArray[2].equals("listRoomIds")) {
				return client.input(inputArray[2]);
			}
			if(inputArray[2].equals("listRooms")) {
				return client.input(inputArray[2]);
			}
			if(inputArray[2].equals("listServers")) {
				return client.input(inputArray[2]);
			}
			if(inputArray[2].equals("help")) {
				result.add(HELP_MESSAGE);
			}
		}
		return result;
	}

	public String formatMessage(String input) {
		String result = input.replaceAll("\\*\\*", "\\\\*\\\\*");
		return result;
	}

	private String getPassword() {
		String env = System.getenv("BCDICE_PASSWORD");
		if(env == null) {
			String password = RandomStringUtils.randomAscii(16); 
			System.out.println("Discord Access Password: " + password);
			return password;
		} else {
			System.out.println("Discord Access Password is written in environment variable BCDICE_PASSWORD");
			return env;
		}
	}
}
