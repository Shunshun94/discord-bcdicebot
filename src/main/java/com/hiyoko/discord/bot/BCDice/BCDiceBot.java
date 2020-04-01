package com.hiyoko.discord.bot.BCDice;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.message.MessageAuthor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;

/**
 * First kicked class for discord-bcdicebot.
 * It creates two instances, DiscordAPI client and BCDice client.
 * @author @Shunshun94
 */
public class BCDiceBot {

	/**
	 * Constructor.
	 * @param token Discord bot token
	 * @param bcDiceUrl BCDice-API URL
	 */
	public BCDiceBot(String token, String bcDiceUrl) {
		new BCDiceBot(token, bcDiceUrl, true);
	}

	/**
	 * @param token Discord bot token
	 * @param bcDiceUrl BCDice-API URL
	 * @param errorSensitive
	 */
	public BCDiceBot(String token, String bcDiceUrl, boolean errorSensitive) {
		BCDiceCLI bcDice = new BCDiceCLI(bcDiceUrl, errorSensitive);
		final Logger logger = LoggerFactory.getLogger(BCDiceBot.class);
		new DiscordApiBuilder().setToken(token).login().thenAccept(api -> {
			
			String myId = api.getYourself().getIdAsString();
			api.addMessageCreateListener(event -> {
				String channel = event.getChannel().getIdAsString();
				MessageAuthor user = event.getMessageAuthor();
				String name = user.getName();
				String userId = user.getIdAsString();
				String message = event.getMessage().getContent();
				logger.debug(String.format("%s: %s", userId, message));
				api.updateActivity("bcdice help とチャットに打ち込むとコマンドのヘルプを確認できます");
				if( myId.equals(userId) ) { return; }
				if(! bcDice.isRoll( message )) {
					logger.debug("bcdice command");
					bcDice.inputs(message, userId, channel).forEach(msg->{
						event.getChannel().sendMessage(msg);
					});
					return;
				}

				try {
					DicerollResult rollResult = bcDice.roll(message, channel);
					logger.debug("Dice command request for dice server is done");
					if(rollResult.isError()) {
						throw new IOException(rollResult.getText());
					}
					if( rollResult.isRolled() ) {
						event.getChannel().sendMessage(String.format("＞%s\n%s", name, rollResult.toString()));
					}
					if( rollResult.isSecret() ) {
						int index = Integer.parseInt(bcDice.input("bcdice save " + rollResult.getSystem() + rollResult.getText(), userId));
						try {
							api.getUserById(userId).get().sendMessage(rollResult.getSystem() + rollResult.getText());
							api.getUserById(userId).get().sendMessage("To recall this,\nbcdice load " + index);
						} catch (InterruptedException e) {
							throw new IOException(e.getMessage(), e);
						} catch (ExecutionException e) {
							throw new IOException(e.getMessage(), e);
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
				System.out.println("Discord-BCDicebot Version 1.11");
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
			}	}

}
