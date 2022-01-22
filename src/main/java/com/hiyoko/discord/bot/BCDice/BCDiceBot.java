package com.hiyoko.discord.bot.BCDice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * First kicked class for discord-bcdicebot.
 * It creates two instances, DiscordAPI client and BCDice client.
 * @author @Shunshun94
 */
public class BCDiceBot {
	final Logger logger = LoggerFactory.getLogger(BCDiceBot.class);
	final DiscordApi api;
	final BCDiceCLI bcDice;

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
	 * @throws IOException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public BCDiceBot(String token, String bcDiceUrl, boolean errorSensitive, String password) throws IOException, InterruptedException, ExecutionException {
		bcDice = new BCDiceCLI(getUrlList(bcDiceUrl), getDefaultSystem(), errorSensitive, password);
		api = new DiscordApiBuilder().setToken(token).login().get();
		api.addMessageCreateListener(new StandardInputMessageCreateListener(api, bcDice));
	}

	private static String getVersion() {
		String version = BCDiceBot.class.getPackage().getImplementationVersion();
		if(version == null) {
			return "See pom.xml file";
		} else {
			return version;
		}
	}

	/**
	 * First called method.
	 * @param args command line parameters. 1st should be Discord bot token. 2nd should be the URL of BCDice-API.
	 * @throws IOException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		if( args.length < 2 || args[0].equals("help") ||
			args[0].equals("--help") || args[0].equals("--h") || args[0].equals("-h")) {
			System.out.println(String.format("Discord-BCDicebot Version %s", getVersion()));
			System.out.println("This application requires two params");
			System.out.println("  1. Discord Bot Token");
			System.out.println("  2. BCDice-api server URL");
			System.out.println("  3. (Optional) Error Handling Flag, When BCDice-API returns Error, If an error message should be sent, it's 0. If not, it's 1.");
			System.out.println("------------------------------------");
			System.out.println("2つコマンドライン引数が必要です");
			System.out.println("  1. Discord の bot token");
			System.out.println("  2. BCDice-api の URL");
			System.out.println("  3. (必要ならば) エラーハンドルフラグ。BCDice-API でエラー発生時にエラーメッセージを出力するなら0 しないなら1");

			return;
		}

		String password = AdminPasswordGenerator.getPassword();

		if(args.length == 2) {
			new BCDiceBot(args[0].trim(), args[1].trim(), true, password);
		} else {
			new BCDiceBot(args[0].trim(), args[1].trim(), args[2].trim().equals("0"), password);
		}
	}
}
