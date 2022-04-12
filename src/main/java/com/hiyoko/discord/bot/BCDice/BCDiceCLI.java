package com.hiyoko.discord.bot.BCDice;

import org.javacord.api.entity.message.MessageAttachment;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.hiyoko.discord.bot.BCDice.AdminCommand.AdminCommand;
import com.hiyoko.discord.bot.BCDice.AdminCommand.AdminCommandsMapFactory;
import com.hiyoko.discord.bot.BCDice.ConfigCommand.ConfigCommand;
import com.hiyoko.discord.bot.BCDice.ConfigCommand.ConfigCommandsMapFactory;
import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;
import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClientFactory;
import com.hiyoko.discord.bot.BCDice.DiceClient.SavedMessageFactory;
import com.hiyoko.discord.bot.BCDice.OriginalDiceBotClients.OriginalDiceBotClient;
import com.hiyoko.discord.bot.BCDice.OriginalDiceBotClients.OriginalDiceBotClientFactory;
import com.hiyoko.discord.bot.BCDice.SystemClient.SystemClient;
import com.hiyoko.discord.bot.BCDice.SystemClient.SystemClientFactory;
import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;
import com.hiyoko.discord.bot.BCDice.dto.OriginalDiceBotTable;
import com.hiyoko.discord.bot.BCDice.dto.SecretMessage;

import org.slf4j.Logger;

/**
 * Client for BCDice.
 * The instance gets the command as String.
 * If it's required, dispatch the command to BCDice.
 * @author @Shunshun94
 *
 */
public class BCDiceCLI {
	private DiceClient client;

	private Map<String, Map<String, SecretMessage>> savedMessage = SavedMessageFactory.getSavedMessages();
	private String password;
	private String rollCommand = "";
	private boolean isSuppressed = true;
	private final OriginalDiceBotClient originalDiceBotClient;
	private final Map<String, AdminCommand> adminCommands = AdminCommandsMapFactory.getAdminCommands();
	private final Map<String, ConfigCommand> configCommands;
	private final SystemClient systemClient;
	private static final String[] REMOVE_WHITESPACE_TARGETS = {"<", ">", "="};
	private final Logger logger = LoggerFactory.getLogger(BCDiceCLI.class);
	private static final Pattern GAMESYSTEM_ROOM_PAIR_REGEXP = Pattern.compile("^(\\d*):(.*)");
	private static final Pattern MULTIROLL_OFFICIAL_PREFIX = Pattern.compile("^(x|rep|repeat)(\\d+)");
	private static final Pattern MULTIROLL_NUM_PREFIX = Pattern.compile("^(\\d+) ");
	private static final String MULTIROLL_TEXT_PREFIX_STR = "^\\[(.+)\\] ";
	private static final Pattern MULTIROLL_TEXT_PREFIX = Pattern.compile(MULTIROLL_TEXT_PREFIX_STR);

	public static final String HELP = "使い方\n"
			+ "# システムの一覧を確認する\n> bcdice list\n\n"
			+ "# 現在のチャンネルで利用するシステムを変更する\n> bcdice set システム名\n\n"
			+ "# システムのヘルプを表示する\n> bcdice help SYSTEM_NAME\n\n"
			+ "# 本ボットの現在の設定を確認する\n> bcdice status\n\n"
			+ "# 管理用コマンド\n> bcdice admin PASSWORD COMMAND";
	public static final String HELP_ADMIN = "使い方\n"
			+ "# admin のヘルプを表示する\n> bcdice admin help\n\n"
			+ "# BCDice-API サーバを変更する\n> bcdice admin PASSWORD setServer URL\n\n"
			+ "# BCDice-API サーバを一覧から削除する\n> bcdice admin PASSWORD removeServer URL\n\n"
			+ "# 利用する BCDice-API サーバの一覧を出す\n> bcdice admin PASSWORD listServer\n\n"
			+ "# 部屋設定をエクスポートする\n> bcdice admin PASSWORD export\n\n"
			+ "# 特定の部屋設定をエクスポートする\n> bcdice admin PASSWORD export ROOM_ID1 ROOM_ID2 ROOM_ID3 ....\n\n"
			+ "# 部屋設定をインポートする\n> bcdice admin PASSWORD import\n\n"
			+ "# ダイスが振られる条件について BCDice API サーバの情報に基づいて更新する\n"
			+ "> bcdice admin PASSWORD updateDiceRollPreFix\n\n"
			+ "# BCDice API サーバへのコマンド送信に接頭詞を求めない（デフォルトの挙動）\n"
			+ "> bcdice admin PASSWORD suppressroll\n"
			+ "> bcdice admin PASSWORD suppressroll on # こっちは近い将来廃止予定\n\n"
			+ "# コマンドの先頭に何らかのコマンドがある場合のみBCDice API サーバへコマンドを送信する\n"
			+ "> bcdice admin PASSWORD suppressroll /diceroll # /diceroll 2d6 等としないとダイスを振れない\n"
			+ "> bcdice admin PASSWORD suppressroll /r # /r 2d6 等としないとダイスを振れない\n\n"
			+ "# BCDice API サーバへの問い合わせの制限を外す （近い将来廃止予定 ～バージョン 1.11 と同じ挙動）\n"
			+ "> bcdice admin PASSWORD suppressroll disable\n\n"
			+ "# ダイスボット表を追加する\n"
			+ "# ダイスボット表のファイルを Discord にアップロードし、アップロードする際のコメントを以下のようにする\n"
			+ "# ダイスボット表名をチャットに書き込むと誰でもダイスボット表を振れる\n"
			+ "> bcdice admin PASSWORD addDiceBot ダイスボット表名\n"
			+ "> bcdice admin PASSWORD addDiceBot # アップロードしたダイスボット表のファイル名がコマンドになる\n\n"
			+ "# ダイスボット表を削除する\n"
			+ "> bcdice admin PASSWORD removeDiceBot ダイスボット表名\n\n"
			+ "# ダイスボット表の一覧を表示する\n"
			+ "> bcdice admin PASSWORD listDiceBot\n\n"
			+ "# 72時間以上前のシークレットダイスの情報を削除する\n"
			+ "> bcdice admin PASSWORD refreshSecretDice";

	/**
	 * @param url BCDice-API URL.
	 * @throws IOException 
	 */
	public BCDiceCLI(String url, OriginalDiceBotClient originalDiceBotClientParam, String password) throws IOException {
		client = DiceClientFactory.getDiceClient(url);
		systemClient = SystemClientFactory.getSystemClient(url);
		try {
			Map<String, String> roomSystemMap = systemClient.getSystemList();
			for(Map.Entry<String, String> roomSystem : roomSystemMap.entrySet() ) {
				client.setSystem(roomSystem.getValue(), roomSystem.getKey());
			}
		} catch(IOException e) {
			logger.warn("チャンネルごとのダイスボット設定のファイルからの読み込みに失敗しましたが、処理は続行します。必要ならば管理コマンドまたは各チャンネル毎に再設定してください", e);
		}
		originalDiceBotClient = originalDiceBotClientParam;
		configCommands = ConfigCommandsMapFactory.getConfigCommands(SavedMessageFactory.getSavedMessages());
		this.password = password;
	}

	public BCDiceCLI(List<String> urls, String system, boolean errorSensitive, String password) throws IOException {
		client = DiceClientFactory.getDiceClient(urls, errorSensitive);
		client.setSystem(system);
		originalDiceBotClient = OriginalDiceBotClientFactory.getOriginalDiceBotClient();
		configCommands = ConfigCommandsMapFactory.getConfigCommands(SavedMessageFactory.getSavedMessages());
		systemClient = SystemClientFactory.getSystemClient(urls.get(0));
		try {
			Map<String, String> roomSystemMap = systemClient.getSystemList();
			for(Map.Entry<String, String> roomSystem : roomSystemMap.entrySet() ) {
				client.setSystem(roomSystem.getValue(), roomSystem.getKey());
			}
		} catch(IOException e) {
			logger.warn("チャンネルごとのダイスボット設定の読み込みに失敗しましたが、処理は続行します。必要ならば管理コマンドまたは各チャンネル毎に再設定してください", e);
		}
		this.password = password;
	}

	public DiceClient getDiceClient() {
		return client;
	}

	public String getRollCommand() {
		return rollCommand;
	}

	public Map<String, ConfigCommand> getConfigCommands() {
		return configCommands;
	}

	/**
	 * @param inputted command
	 * @return If the command is for roll dice command, true. If not false
	 */
	public boolean isRoll(String input) {
		return ! (input.toLowerCase().startsWith("bcdice ") || input.toLowerCase().equals("bcdice"));
	}

	private boolean isShouldRoll(String input, String system) throws IOException {
		if(! isSuppressed) { return true; } //TODO 2021/08/22 近々廃止する
		if( rollCommand.isEmpty() ) {
			return client.isDiceCommand(input.trim(), system);
		} else {
			return input.startsWith(rollCommand);
		}
	}

	public String serachOriginalDicebot(String input) {
		List<String> list = originalDiceBotClient.getDiceBotList();
		for(String name : list) {
			if(input.startsWith(name)) {return name;}
		}
		return "";
	}

	private String isOriginalDicebot(String rawInput) {
		if(! (rollCommand.isEmpty() || rawInput.startsWith(rollCommand))) {
			return "";
		}
		return serachOriginalDicebot(rawInput.replaceFirst(rollCommand, "").trim());
	}
	
	private List<DicerollResult> rollOriginalDiceBotMultiple(OriginalDiceBotTable dbt, int times) throws IOException {
		logger.debug(String.format("ダイスボット表 [%s] を%s回 実行します", dbt.getName(), times));
		try {
			DicerollResult tmp = client.rollDice(String.format("x%s %s", times, dbt.getCommand()));
			return dbt.getResultsAsInvalidTable(tmp.getText()).stream().map(t->{
				return new DicerollResult(t, "DiceBot", false, true, false);
			}).collect(Collectors.toList());
		} catch(IOException e) {
			throw new IOException(String.format("[ERROR] %s", e.getMessage()));
		}
	}

	private DicerollResult rollOriginalDiceBot(String name) throws IOException {
		OriginalDiceBotTable diceBot = null;
		try {
			diceBot = originalDiceBotClient.getDiceBot(name);
		} catch (IOException e) {
			throw new IOException(String.format("ダイスボット表 [%s] が取得できませんでした", name), e);
		}
		return rollOriginalDiceBotMultiple(diceBot, 1).get(0);
	}

	public List<DicerollResult> rolls(String rawInput, String channel) throws IOException {
		List<DicerollResult> result = new ArrayList<DicerollResult>();
		if(! (rollCommand.isEmpty() || rawInput.trim().startsWith(rollCommand))) {
			return result;
		}
		rawInput = rawInput.replaceAll("\\h", " ");
		String input = rawInput.replaceFirst(rollCommand, "").trim();

		Matcher isOfficialMultiRollMatcher = MULTIROLL_OFFICIAL_PREFIX.matcher(input);
		if(isOfficialMultiRollMatcher.find()) {
			String withoutRepeat = input.replaceFirst(isOfficialMultiRollMatcher.group(), "").trim();
			String originalDiceBot = isOriginalDicebot(String.format("%s%s", rollCommand, withoutRepeat));
			if(! originalDiceBot.isEmpty()) {
				OriginalDiceBotTable diceBot = null;
				try {
					diceBot = originalDiceBotClient.getDiceBot(originalDiceBot);
				} catch (IOException e) {
					throw new IOException(String.format("ダイスボット表 [%s] が取得できませんでした", originalDiceBot), e);
				}
				int times = Integer.parseInt(isOfficialMultiRollMatcher.group(2));
				return rollOriginalDiceBotMultiple(diceBot, times);
			}
		}

		Matcher isNumMatcher = MULTIROLL_NUM_PREFIX.matcher(input);
		if(isNumMatcher.find()) { //TODO いずれ消す。オフィシャルの繰り返しコマンドで置換されるべきでは
			String rawCount = isNumMatcher.group(1);
			String withoutRepeat = input.replaceFirst(rawCount, "").trim();
			String originalDiceBot = isOriginalDicebot(String.format("%s%s", rollCommand, withoutRepeat));
			if(! originalDiceBot.isEmpty()) {
				OriginalDiceBotTable diceBot = null;
				try {
					diceBot = originalDiceBotClient.getDiceBot(originalDiceBot);
				} catch (IOException e) {
					throw new IOException(String.format("ダイスボット表 [%s] が取得できませんでした", originalDiceBot), e);
				}
				int times = Integer.parseInt(rawCount);
				return rollOriginalDiceBotMultiple(diceBot, times);
			} else {
				rawInput = String.format("%s repeat%s %s", rollCommand, rawCount, withoutRepeat).trim();
			}
		}
		Matcher isTextMatcher = MULTIROLL_TEXT_PREFIX.matcher(input);
		if(isTextMatcher.find()) {
			String rawTargetList = isTextMatcher.group(1);
			String[] targetList = rawTargetList.split(",");
			String requiredCommand = String.format("%s%s" , rollCommand, input.replaceFirst(MULTIROLL_TEXT_PREFIX_STR, "").trim());
			if(targetList.length > 20) {
				String system = client.getSystem(channel);
				if( isOriginalDicebot(requiredCommand).isEmpty() && (! isShouldRoll(requiredCommand, system)) ) {
					return result;
				} else {
					throw new IOException(String.format("1度にダイスを振れる回数は20回までです（%d回振ろうとしていました）", targetList.length));
				}
			}
			for(String target: targetList) {
				DicerollResult tmpResult = roll(requiredCommand, channel);
				result.add( new DicerollResult(
						String.format("#%s\n%s", target, tmpResult.getText()),
								tmpResult.getSystem(),
								tmpResult.isSecret(),
								tmpResult.isRolled(),
								tmpResult.isError()
						));
			}
			return result;
		}
		DicerollResult tmpResult = roll(rawInput, channel);
		if(tmpResult.isRolled() || tmpResult.isError()) {
			result.add(tmpResult);
		} 
		return result;
	}

	/**
	 * @param rawInput Dice roll command
	 * @param channel
	 * @return result as DicerollResult instance.
	 * @throws IOException When command failed
	 */
	public DicerollResult roll(String rawInput, String channel) throws IOException {
		String originalDiceBot = isOriginalDicebot(rawInput);
		if(! originalDiceBot.isEmpty()) {
			return rollOriginalDiceBot(originalDiceBot);
		}
		String system = client.getSystem(channel);
		if(isShouldRoll(rawInput, system)) {
			String input = rawInput.replaceFirst(rollCommand, "").trim();
			logger.debug(String.format("bot send command to server: %s", input));
			return client.rollDiceWithChannel(normalizeDiceCommand(input), channel);
		} else {
			return new DicerollResult("", "", false, false);
		}
	}

	/**
	 * @param tmpInput (not dice roll)
	 * @param id unique user id
	 * @param channel action target channel
	 * @return message from this instance
	 */
	public List<String> inputs(String tmpInput, String id, String channel) {
		return inputs(tmpInput, id, channel, new ArrayList<MessageAttachment>());
	}

	public List<String> separateStringWithLengthLimitation(List<String> raw, int limitLength) {
		List<String> result = new ArrayList<String>();
		StringBuilder sb = new StringBuilder("");
		raw.forEach(line->{
			sb.append(line + "\n");
			if(sb.length() > limitLength) {
				result.add(sb.toString());
				sb.delete(0, sb.length());
			}
		});
		result.add(sb.toString());
		return result;
	}

	public List<String> separateStringWithLengthLimitation(String raw, int limitLength) {
		return separateStringWithLengthLimitation(Arrays.asList(raw.split("\\n")), limitLength);
	}

	/**
	 * 
	 * @param tmpInput (not dice roll)
	 * @param id unique user id
	 * @param channel action target channel
	 * @param attachements
	 * @return message from this instance
	 */
	public List<String> inputs(String tmpInput, String id, String channel, List<MessageAttachment> attachements) {
		List<String> resultList = new ArrayList<String>();
		
		String input = tmpInput.split("\n")[0];
		String[] command = input.split(" ");
		if(command.length == 1) {
			resultList.add(HELP);
			return resultList;
		}
		if(command[1].equals("help")) {
			if(command.length > 2) {
				String systemName = String.join(" ", Arrays.copyOfRange(command, 2, command.length));
				return configCommands.get("help").exec(systemName, client, id, channel);
			}
		}
		if(command[1].equals("set")) {
			if(command.length > 2) {
				String systemName = String.join(" ", Arrays.copyOfRange(command, 2, command.length));
				return configCommands.get("set").exec(systemName, client, id, channel);
			} else {
				resultList.add(
						"[ERROR] ダイスボットのシステムを変更するには次のコマンドを打つ必要があります\n"
						+ "　 bcdice set SYSTEM_NAME\n"
						+ "例 bcdice set AceKillerGene");
				return resultList;
			}
		}
		if(command[1].equals("list")) {
			return configCommands.get("list").exec("", client, id, channel);
		}

		if(command[1].equals("load")) {
			if(command.length == 3) {
				return configCommands.get("load").exec(command[2], client, id, channel);
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
			return configCommands.get("status").exec("", client, id, channel);
		}
		if(command[1].equals("admin")) {
			if( command.length < 4 ) {
				resultList.add(HELP_ADMIN);
				return resultList;
			} else {
				return adminCommand(command, tmpInput, attachements);
			}
		}
		resultList.add(HELP);
		return resultList;
	}

	private List<String> adminCommand(String[] command, String tmpInput, List<MessageAttachment> attachements) {
		List<String> resultList = new ArrayList<String>();
		if(command[2].equals("help")) {
			resultList.add(HELP_ADMIN);
			return resultList;
		}
		if(! command[2].equals(password)) {
			resultList.add("パスワードが違います");
			return resultList;
		}
		if(command[3].equals("listServer")) {
			return separateStringWithLengthLimitation(adminCommands.get("listserver").exec("", client), 1000);
		}
		if(command[3].equals("removeServer")) {
			if(command.length < 5) {
				resultList.add("URL が足りません");
				resultList.add(HELP_ADMIN);
				return resultList;
			} else {
				return adminCommands.get("removeserver").exec(command[4], client);
			}
		}
		if(command[3].equals("setServer")) {
			if(command.length < 5) {
				resultList.add("URL が足りません");
				resultList.add(HELP_ADMIN);
				return resultList;
			} else {
				return adminCommands.get("removeserver").exec(command[4], client);
			}
		}
		if(command[3].equals("export")) {
			Map<String, String> roomList = client.getRoomsSystem();
			resultList.add("Room-System List\n");
			if(command.length == 4) {
				resultList.addAll(separateStringWithLengthLimitation(roomList.entrySet().stream().map(p -> String.format("%s:%s", p.getKey(), p.getValue() )).collect(Collectors.toList()), 1000));
				return resultList;
			} else {
				for(int i = 4; i < command.length; i++) {
					String roomSystem = roomList.get(command[i]);
					if(roomSystem != null) {
						resultList.add(String.format("%s:%s", command[i], roomSystem));
					}
				}
				return separateStringWithLengthLimitation(resultList, 1000);
			}
		}
		if(command[3].equals("import")) {
			String[] originalLines = tmpInput.split("\n");
			String[] diceBotRoomList = Arrays.copyOfRange(originalLines, 1, originalLines.length);
			for(String line: diceBotRoomList) {
				Matcher matchResult = GAMESYSTEM_ROOM_PAIR_REGEXP.matcher(line);
				if(matchResult.find()) {
					client.setSystem(matchResult.group(2), matchResult.group(1));
					resultList.add("Room" + matchResult.group(1) + " -> " + matchResult.group(2));
				}
			}
			try {
				systemClient.exportSystemList(client.getRoomsSystem());
			} catch(IOException e) {
				logger.warn("チャンネルごとのダイスボット設定のファイルへの書き出しに失敗しましたが、処理は続行します", e);
			}
			return separateStringWithLengthLimitation(resultList, 1000);
		}
		if(command[3].equals("updateDiceRollPreFix")) {
			return separateStringWithLengthLimitation(adminCommands.get("updatedicerollprefix").exec("", client),1000);
		}
		if(command[3].equals("suppressroll")) {
			if(command.length > 4) {
				if(command[4].equals("disable")) {
					isSuppressed = false;
					rollCommand = "";
					resultList.add("BCDice API サーバに送信するコマンドの制限を解除しました。すべてのコマンドがサーバに送信されます");
				} else {
					isSuppressed = true;
					if(command[4].startsWith("/")) {
						rollCommand = command[4];
						resultList.add(String.format("BCDice API サーバに送信するコマンドを制限しました。 \"%s\" で始まるコマンドのみサーバに送信します ", command[4]));
					} else {
						rollCommand = "";
						resultList.add("BCDice API サーバに送信するコマンドを制限しました。まずコマンドじゃないだろう、という内容はサーバに送信しません。");
					}
				}
			} else {
				// suppress roll を有効にする
				isSuppressed = true;
				rollCommand = "";
				resultList.add("BCDice API サーバに送信するコマンドを制限しました。まずコマンドじゃないだろう、という内容はサーバに送信しません。");
			}
			return resultList;
		}

		if(command[3].equals("addDiceBot")) {
			if(attachements.isEmpty()) {
				resultList.add("ダイスボット表を登録する際はダイスボット表のファイルをアップロードする必要があります");
				return resultList;
			}

			try {
				String botName = (command.length > 4) ? command[4] : attachements.get(0).getFileName().split("\\.")[0];
				URL url = attachements.get(0).getUrl();
				originalDiceBotClient.registerDiceBot(url, botName);
				String logMessage = String.format("ダイスボット表 [%s] を登録しました", botName);
				logger.info(logMessage);
				resultList.add(logMessage);
				return resultList;
			} catch(Exception e) {
				logger.warn("ダイスボット表の登録に失敗しました", e);
				resultList.add(e.getMessage());
				return resultList;
			}
		}
		if(command[3].equals("removeDiceBot")) {
			if(command.length < 5) {
				resultList.add("ダイスボット表の名前を指定してください");
				return resultList;
			}
			return separateStringWithLengthLimitation(adminCommands.get("removeoriginaltable").exec(command[4], client), 1000);
		}
		if(command[3].equals("listDiceBot")) {
			return separateStringWithLengthLimitation(adminCommands.get("listoriginaltable").exec("", client),1000);
		}
		if(command[3].equals("refreshSecretDice")) {
			return separateStringWithLengthLimitation(adminCommands.get("refreshsecretdice").exec("", client),1000);
		}

		resultList.add(HELP_ADMIN);
		return resultList;
	}

	/**
	 * Stacking secret dice result
	 * @param id user unique id
	 * @param message stacked message
	 * @return The stacked message index
	 */
	public String saveMessage(String id, String message) {
		List<String> currentMessage = new ArrayList<String>();
		currentMessage.add(message);
		return saveMessage(id, currentMessage);
	}

	public String saveMessage(String id, List<String> messages) {
		// savedMessage
		Map<String, SecretMessage> msgList = savedMessage.get(id);
		if(msgList == null) {
			msgList = new HashMap<String, SecretMessage>();
			savedMessage.put(id, msgList);
		}
		
		SecretMessage secretMessage = new SecretMessage(messages);
		String key = String.valueOf(secretMessage.getLimit());
		while(msgList.containsKey(key)) {
			key = key + '0';
		}
		msgList.put(key, secretMessage);
		logger.info(String.format("Message saved User:%s Key:%s - %s (%s)", id, key, messages.get(0), msgList.size()));
		return String.valueOf(key);
	}

	/**
	 * Normalizer for the commands.
	 * See also https://github.com/Shunshun94/discord-bcdicebot/pull/10
	 * @param command raw command
	 * @return Normalized command.
	 * @throws IOException 
	 */
	private String normalizeDiceCommand(String rawCommand) throws IOException {
		String command = rawCommand;
		try {
			for(String replaceTarget: REMOVE_WHITESPACE_TARGETS) {
				command = command.replaceAll("[\\s　]*[" + replaceTarget + "]+[\\s　]*", replaceTarget);
			}
			command = URLEncoder.encode(command.replaceAll(" ", "%20"), "UTF-8");
			command = command.replaceAll("%2520", "%20").replaceAll("%7E", "~");
			return command;
		} catch (UnsupportedEncodingException e) {
			throw new IOException("Failed to encode [" + rawCommand + "]", e);
		}
	}

	public static void main(String[] args) throws IOException {
		String password = AdminPasswordGenerator.getPassword();
		BCDiceCLI cli = new BCDiceCLI(args[0].trim(), OriginalDiceBotClientFactory.getOriginalDiceBotClient(), password);

		String line;
		Scanner scanner = new Scanner(System.in);
		while(scanner.hasNext()) {
			line = scanner.nextLine().trim();
			System.out.println(String.format("INPUT:%s", line));
			if(cli.isRoll(line)) {
				try {
					List<DicerollResult> rollResults = cli.rolls(line, "cli");
					rollResults.forEach(rollResult->{
						if(rollResult.isRolled()) {
							System.out.println(rollResult.getText());
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				cli.inputs(line, "dummy", "cli").forEach(str->System.out.println(str));
			}
		}
		scanner.close();
	}
}
