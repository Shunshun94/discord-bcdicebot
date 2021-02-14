package com.hiyoko.discord.bot.BCDice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.MessageAuthor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiyoko.discord.bot.BCDice.ChatTool.ChatToolClient;
import com.hiyoko.discord.bot.BCDice.ChatTool.ChatToolClientFactory;
import com.hiyoko.discord.bot.BCDice.NameIndicator.NameIndicator;
import com.hiyoko.discord.bot.BCDice.NameIndicator.NameIndicatorFactory;
import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;

/**
 * First kicked class for discord-bcdicebot.
 * It creates two instances, DiscordAPI client and BCDice client.
 * @author @Shunshun94
 */
public class BCDiceBot {
	final Logger logger = LoggerFactory.getLogger(BCDiceBot.class);
	/**
	 * Constructor.
	 * @param token Discord bot token
	 * @param bcDiceUrl BCDice-API URL
	 */
	public BCDiceBot(String token, String bcDiceUrl) {
		new BCDiceBot(token, bcDiceUrl, true);
	}

	private List<String> getUrlList(String bcDiceUrl) {
		List<String> urlList = new ArrayList<String>();
		urlList.add(bcDiceUrl);
		String secondaryUrl = System.getenv("BCDICE_API_SECONDARY");
		if(secondaryUrl != null) {
			urlList.add(secondaryUrl);
			logger.info(String.format("  Primary URL: %s", bcDiceUrl));
			logger.info(String.format("Secondary URL: %s", secondaryUrl));
		}
		return urlList;
	}

	private String getDefaultSystem() {
		String defaultSystem = System.getenv("BCDICE_DEFAULT_SYSTEM");
		if(defaultSystem == null) {
			return "DiceBot";
		} else {
			return defaultSystem;
		}
	}

	/**
	 * @param token Discord bot token
	 * @param bcDiceUrl BCDice-API URL
	 * @param errorSensitive
	 */
	public BCDiceBot(String token, String bcDiceUrl, boolean errorSensitive) {
		BCDiceCLI bcDice = new BCDiceCLI(getUrlList(bcDiceUrl), getDefaultSystem(), errorSensitive);
		NameIndicator nameIndicator = NameIndicatorFactory.getNameIndicator();
		new DiscordApiBuilder().setToken(token).login().thenAccept(api -> {
			String myId = api.getYourself().getIdAsString();
			ChatToolClient chatToolClient = ChatToolClientFactory.getChatToolClient(api);
			api.addMessageCreateListener(event -> {
				String channel = event.getChannel().getIdAsString();
				MessageAuthor user = event.getMessageAuthor();
				String name = nameIndicator.getName(user);
				String userId = user.getIdAsString();
				String message = event.getMessage().getContent();
				List<MessageAttachment> attachements = event.getMessage().getAttachments();

				logger.debug(String.format("%s posts: https://discordapp.com/channels/%s/%s/%s",
						userId,
						event.getServer().get().getIdAsString(), channel, event.getMessage().getIdAsString()));
				api.updateActivity("bcdice help とチャットに打ち込むとコマンドのヘルプを確認できます");
				if( myId.equals(userId) ) { return; }
				if(chatToolClient.isRequest( message )) {
					List<String> result = chatToolClient.input(message);
					if(! result.isEmpty()) {
						bcDice.separateStringWithLengthLimitation(result, 1000).forEach(p->event.getChannel().sendMessage(p));
						return;
					}
				}
				if(! bcDice.isRoll( message )) {
					bcDice.inputs(message, userId, channel, attachements).forEach(msg->{
						event.getChannel().sendMessage(msg);
					});
					return;
				}

				try {
					List<DicerollResult> rollResults = bcDice.rolls(message, channel);
					if(rollResults.size() > 0) {
						logger.debug("Dice command request for dice server is done");
						List<String> sb = new ArrayList<String>();
						for(DicerollResult rollResult : rollResults) {
							if(rollResult.isError()) {
								throw new IOException(rollResult.getText());
							}
							if( rollResult.isRolled() ) {
								sb.add(rollResult.toString());
							}
						}
						bcDice.separateStringWithLengthLimitation(String.format("＞%s\n%s", name, sb.stream().collect(Collectors.joining("\n\n"))), 1000).forEach((post)->{
							event.getChannel().sendMessage(post);
						});
						DicerollResult firstOne = rollResults.get(0); 
						if( firstOne.isSecret() ) {
							int index = Integer.parseInt(bcDice.inputs("bcdice save " + firstOne.getSystem() + firstOne.getText(), userId, "dummy").get(0));
							try {
								api.getUserById(userId).get().sendMessage(firstOne.getSystem() + firstOne.getText());
								api.getUserById(userId).get().sendMessage("To recall this,\nbcdice load " + index);
							} catch (InterruptedException e) {
								throw new IOException(e.getMessage(), e);
							} catch (ExecutionException e) {
								throw new IOException(e.getMessage(), e);
							}
						}
					}
				} catch (IOException e) {
					event.getChannel().sendMessage(String.format("＞%s\n[ERROR]%s", name, e.getMessage()));
					logger.warn(String.format("USERID: %s MESSAGE: %s", userId, message));
					logger.warn("Failed to reply to user request", e);
				}
			});
		});
	}

	/**
	 * First called method.
	 * @param args command line parameters. 1st should be Discord bot token. 2nd should be the URL of BCDice-API.
	 */
	public static void main(String[] args) {
		if( args.length < 2 || args[0].equals("help") ||
			args[0].equals("--help") || args[0].equals("--h") || args[0].equals("-h")) {
			System.out.println("Discord-BCDicebot Version 2.2.0");
			System.out.println("This application requires two params");
			System.out.println("  1. Discord Bot Token");
			System.out.println("  2. BCDice-api server URL");
			System.out.println("  3. (Optional) Error Handling Flag, When BCDice-API returns Error, If an error message should be sent, it's 0. If not, it's 1.");
			System.out.println("------------------------------------");
			System.out.println("2つコマンドライン引数が必要です");
			System.out.println("  1. Discord の bot token");
			System.out.println("  2. BCDice-api の URL");
			System.out.println("  3. (必要ならば) エラーハンドルフラグ。BCDice-API でエラー発生時にエラーメッセージを出力するなら0 しないなら1");
		} else if(args.length == 2) {
			new BCDiceBot(args[0].trim(), args[1].trim());
		} else {
			new BCDiceBot(args[0].trim(), args[1].trim(), args[2].trim().equals("0"));
		}
	}
}
