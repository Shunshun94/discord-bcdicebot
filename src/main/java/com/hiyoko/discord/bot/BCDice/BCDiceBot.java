package com.hiyoko.discord.bot.BCDice;

import java.io.IOException;

import com.google.common.util.concurrent.FutureCallback;
import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;

import de.btobastian.javacord.DiscordAPI;
import de.btobastian.javacord.Javacord;
import de.btobastian.javacord.entities.message.Message;
import de.btobastian.javacord.listener.message.MessageCreateListener;

/**
 * First kicked class for discord-bcdicebot.
 * It creates two instances, DiscordAPI client and BCDice client.
 * @author @Shunshun94
 *
 */
public class BCDiceBot {
	private DiscordAPI api;
	private BCDiceCLI bcDice;
	
	/**
	 * Constructor.
	 * @param token Discord bot token
	 * @param bcDiceUrl BCDice-API URL
	 */
	public BCDiceBot(String token, String bcDiceUrl) {
		new BCDiceBot(token, bcDiceUrl, true);
	}
	
	/**
	 * 
	 * @param token Discord bot token
	 * @param bcDiceUrl BCDice-API URL
	 * @param errorSensitive
	 */
	public BCDiceBot(String token, String bcDiceUrl, boolean errorSensitive) {
		api = Javacord.getApi(token, true);
		bcDice = new BCDiceCLI(bcDiceUrl, errorSensitive);
		
		api.connect(new FutureCallback<DiscordAPI>() {
			@Override
			public void onSuccess(DiscordAPI api) {
				api.registerListener(new MessageCreateListener() { 
					@Override
					public void onMessageCreate(DiscordAPI api, Message message) {
						String userId = message.getAuthor().getId();
						if(userId.equals(api.getYourself().getId())) {
							return;
						}
						String channel = message.getChannelReceiver().getName();
						if(bcDice.isRoll(message.getContent())) {
							try {
								DicerollResult rollResult = bcDice.roll(message.getContent(), channel);
								if(rollResult.isRolled()) {
									message.reply(">" + message.getAuthor().getName() + "\n" + rollResult.toString());
								}
								if(rollResult.isSecret()) {
									int index = Integer.parseInt(bcDice.input("bcdice save " + rollResult.getSystem() + rollResult.getText(), userId));
									message.getAuthor().sendMessage(rollResult.getSystem() + rollResult.getText());
									message.getAuthor().sendMessage("To recall this,\nbcdice load " + index);
								}
							} catch (IOException e) {
								message.reply(">" + message.getAuthor().getName() + "\n[ERROR]" + e.getMessage());
							}
						} else {
							message.reply(bcDice.input(message.getContent(), userId, channel));
						}
					}
				});
			}

			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		});
	}

	/**
	 * First called method.
	 * @param args command line parameters. 1st should be Discord bot token. 2nd should be the URL of BCDice-API.
	 */
	public static void main(String[] args) {
		if( args.length < 2 || args[0].equals("help") ||
			args[0].equals("--help") || args[0].equals("--h") || args[0].equals("-h")) {
			System.out.println("This application requires two params");
			System.out.println("  1. Discord Bot Token");
			System.out.println("  2. BCDice-api server URL");
			System.out.println("------------------------------------");
			System.out.println("2つコマンドライン引数が必要です");
			System.out.println("  1. Discord の bot token");
			System.out.println("  2. BCDice-api の URL");
		} else if(args.length == 2) {
			new BCDiceBot(args[0], args[1]);
		} else {
			new BCDiceBot(args[0], args[1], args[2].equals("0"));
		}
	}
}
