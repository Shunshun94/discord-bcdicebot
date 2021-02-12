package com.hiyoko.discord.bot.BCDice.ChatTool;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.javacord.api.DiscordApi;

public class DiscordClient implements ChatToolClient {
	private final DiscordApi api;
	private final String password;
	private final String HELP_MESSAGE = "bcdicediscord コマンドの使い方\n"
			+ "# チャンネルの ID を一覧する\n"
			+ "> bcdicediscord PASSWORD listRoomIds\n"
			+ "# チャンネルを一覧する\n"
			+ "> bcdicediscord PASSWORD listRooms\n"
			+ "# サーバを一覧する\n"
			+ "> bcdicediscord PASSWORD listServers";

	public DiscordClient(DiscordApi api) {
		this.api = api;
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
				return getRoomIds();
			}
			if(inputArray[2].equals("listRooms")) {
				return getRooms();
			}
			if(inputArray[2].equals("listServers")) {
				return getServerList();
			}
			if(inputArray[2].equals("help")) {
				result.add(HELP_MESSAGE);
			}
		}
		return result;
	}

	private List<String> getRoomIds() {
		return api.getChannels().stream().filter(channel->{
			return channel.getType().isTextChannelType();
		}).map(channel->channel.getIdAsString()).collect(Collectors.toList());
	}

	private List<String> getRooms() {
		return api.getChannels().stream().filter(channel->{
			return channel.getType().isTextChannelType();
		}).map(channel->String.format("%s\t%s\t%s", channel.getIdAsString(), channel.asServerChannel().get().getName() , channel.asServerChannel().get().getServer().getName())).collect(Collectors.toList());
	}

	private List<String> getServerList() {
		return api.getServers().stream().map(server->String.format("%s\t%s", server.getIdAsString(), server.getName())).collect(Collectors.toList());
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
