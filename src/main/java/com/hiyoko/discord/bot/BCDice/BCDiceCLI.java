package com.hiyoko.discord.bot.BCDice;

import org.apache.commons.lang3.RandomStringUtils;
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

import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClient;
import com.hiyoko.discord.bot.BCDice.DiceClient.DiceClientFactory;
import com.hiyoko.discord.bot.BCDice.OriginalDiceBotClients.OriginalDiceBotClient;
import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;
import com.hiyoko.discord.bot.BCDice.dto.OriginalDiceBotTable;
import com.hiyoko.discord.bot.BCDice.dto.SystemInfo;
import com.hiyoko.discord.bot.BCDice.dto.VersionInfo;

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
	
	private Map<String, List<String>> savedMessage;
	private String password;
	private String rollCommand = "";
	private boolean isSuppressed = true;
	private final OriginalDiceBotClient originalDiceBotClient;
	private static final String[] REMOVE_WHITESPACE_TARGETS = {"<", ">", "="};
	private final Logger logger = LoggerFactory.getLogger(BCDiceCLI.class);
	private static final Pattern GAMESYSTEM_ROOM_PAIR_REGEXP = Pattern.compile("^(\\d*):(.*)");
	private static final Pattern MULTIROLL_OFFICIAL_PREFIX = Pattern.compile("^(x|rep|repeat)(\\d+)");
	private static final Pattern MULTIROLL_NUM_PREFIX = Pattern.compile("^(\\d+) ");
	private static final String MULTIROLL_TEXT_PREFIX_STR = "^\\[(.+)\\] ";
	private static final Pattern MULTIROLL_TEXT_PREFIX = Pattern.compile(MULTIROLL_TEXT_PREFIX_STR);

	public static final String HELP = "使い方\n"
			+ "# システムの一覧を確認する\n> bcdice list\n"
			+ "# 現在のチャンネルで利用するシステムを変更する\n> bcdice set システム名\n"
			+ "# システムのヘルプを表示する\n> bcdice help SYSTEM_NAME\n"
			+ "# 本ボットの現在の設定を確認する\n> bcdice status\n"
			+ "# 管理用コマンド\n> bcdice admin PASSWORD COMMAND";
	public static final String HELP_ADMIN = "使い方\n"
			+ "# admin のヘルプを表示する\n> bcdice admin help\n"
			+ "# BCDice-API サーバを変更する\n> bcdice admin PASSWORD setServer URL\n"
			+ "# BCDice-API サーバを一覧から削除する\n> bcdice admin PASSWORD removeServer URL\n"
			+ "# 利用する BCDice-API サーバの一覧を出す\n> bcdice admin PASSWORD listServer\n"
			+ "# 部屋設定をエクスポートする\n> bcdice admin PASSWORD export\n"
			+ "# 特定の部屋設定をエクスポートする\\n> bcdice admin PASSWORD export ROOM_ID1 ROOM_ID2 ROOM_ID3 ....\n"
			+ "# 部屋設定をインポートする\n> bcdice admin PASSWORD import\n"
			+ "# BCDice API サーバへの問い合わせを制限し、明らかに不必要なコマンドをサーバに送信しない（デフォルトの挙動）\n"
			+ "> bcdice admin PASSWORD suppressroll\n"
			+ "> bcdice admin PASSWORD suppressroll on # どちらでも可能\n"
			+ "# BCDice API サーバへの問い合わせの制限を外す （～バージョン 1.11 と同じ挙動）\n"
			+ "> bcdice admin PASSWORD suppressroll disable\n"
			+ "# コマンドの先頭に何らかのコマンドがある場合のみBCDice API サーバへ問い合わせる\n"
			+ "> bcdice admin PASSWORD suppressroll /diceroll # /diceroll 2d6 等としないとダイスを振れない\n"
			+ "> bcdice admin PASSWORD suppressroll /r # /r 2d6 等としないとダイスを振れない\n"
			+ "# ダイスボット表を追加する\n"
			+ "# ダイスボット表のファイルを Discord にアップロードし、アップロードする際のコメントを以下のようにする\n"
			+ "# ダイスボット表名をチャットに書き込むと誰でもダイスボット表を振れる\n"
			+ "> bcdice admin PASSWORD addDiceBot ダイスボット表名\n"
			+ "> bcdice admin PASSWORD addDiceBot # アップロードしたダイスボット表のファイル名がコマンドになる\n"
			+ "# ダイスボット表を削除する\n"
			+ "> bcdice admin PASSWORD removeDiceBot ダイスボット表名\n"
			+ "# ダイスボット表の一覧を表示する\n"
			+ "> bcdice admin PASSWORD listDiceBot";

	private String getPassword() {
		String env = System.getenv("BCDICE_PASSWORD");
		if(env == null) {
			String password = RandomStringUtils.randomAscii(16); 
			System.out.println("Admin Password: " + password);
			return password;
		} else {
			System.out.println("Admin Password is written in environment variable BCDICE_PASSWORD");
			return env;
		}
	}

	/**
	 * @param url BCDice-API URL.
	 */
	public BCDiceCLI(String url, OriginalDiceBotClient originalDiceBotClientParam) {
		client = DiceClientFactory.getDiceClient(url);
		originalDiceBotClient = originalDiceBotClientParam;
		savedMessage = new HashMap<String, List<String>>();
		password = getPassword();
	}

	public BCDiceCLI(List<String> urls, String system, boolean errorSensitive) {
		client = DiceClientFactory.getDiceClient(urls, errorSensitive);
		client.setSystem(system);
		originalDiceBotClient = new OriginalDiceBotClient();
		savedMessage = new HashMap<String, List<String>>();
		password = getPassword();
	}

	/**
	 * @param inputted command
	 * @return If the command is for roll dice command, true. If not false
	 */
	public boolean isRoll(String input) {
		return ! (input.toLowerCase().startsWith("bcdice ") || input.toLowerCase().equals("bcdice"));
	}

	private boolean isShouldRoll(String input) {
		if(! isSuppressed) { return true; }
		if( rollCommand.isEmpty() ) {
			return client.isDiceCommand(input);
		} else {
			return input.startsWith(rollCommand);
		}
	}

	private String serachOriginalDicebot(String input) {
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
		List<DicerollResult> list = new ArrayList<DicerollResult>();
		try {
			if(dbt.isValid) {
				for(int i = 0; i < times; i++) {
					list.add(client.rollOriginalDiceBotTable(dbt));
				}
			} else {
				for(int i = 0; i < times; i++) {
					DicerollResult tmp = client.rollDice(dbt.getCommand());
					String value = dbt.getResultAsInvalidTable(tmp.getText());
					list.add(new DicerollResult(value, "DiceBot", false, true, false));
				}
			}
			if(times == 1) {
				return list;
			} else {
				List<DicerollResult> fixedResult = new ArrayList<DicerollResult>();
				for(int i = 0; i < times; i++) {
					DicerollResult targetResult = list.get(i);
					fixedResult.add(new DicerollResult(String.format("#%s\n%s", i + 1, targetResult.getText()), targetResult.getSystem(), false, true, false));
				}
				return fixedResult;
			}
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
		if(isNumMatcher.find()) { // いずれ消す。オフィシャルの繰り返しコマンドで置換されるべきでは
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
				if( isOriginalDicebot(requiredCommand).isEmpty() && (! isShouldRoll(requiredCommand)) ) {
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
		if(isShouldRoll(rawInput)) {
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
				try {
					String originalDicebot = serachOriginalDicebot(systemName);
					if(originalDicebot.isEmpty() ) {
						SystemInfo info = client.getSystemInfo(systemName);
						resultList.add("[" + systemName + "]\n" + info.getInfo());
						return resultList;
					} else {
						OriginalDiceBotTable originalDiceBot = originalDiceBotClient.getDiceBot(originalDicebot);
						String helpMessage = originalDiceBot.getHelp();
						resultList.addAll(separateStringWithLengthLimitation(helpMessage, 1000));
						return resultList;
					}
				} catch (IOException e) {
					resultList.add("[" + systemName + "]\n" + e.getMessage());
					return resultList;
				}
			}
		}
		if(command[1].equals("set")) {
			if(command.length > 2) {
				String systemName = String.join(" ", Arrays.copyOfRange(command, 2, command.length));
				client.setSystem(systemName, channel);
				resultList.add("BCDice system is changed: " + systemName);
				return resultList;
			} else {
				resultList.add(
						"[ERROR] ダイスボットのシステムを変更するには次のコマンドを打つ必要があります\n"
						+ "　 bcdice set SYSTEM_NAME\n"
						+ "例 bcdice set AceKillerGene");
				return resultList;
			}
		}
		if(command[1].equals("list")) {
			try {
				resultList.add("[DiceBot List]");
				resultList.addAll(separateStringWithLengthLimitation(client.getSystems().getSystemList(), 1000));
				return resultList;
			} catch (IOException e) {
				resultList.add(e.getMessage());
				return resultList;
			}
		}

		if(command[1].equals("load")) {
			if(command.length == 3) {
				try {
					resultList.add(getMessage(id, new Integer(command[2])));
					return resultList;
				} catch(Exception e) {
					resultList.add("Not found (index = " + command[2] + ")");
					return resultList;
				}
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
			try {
				VersionInfo vi = client.getVersion();
				resultList.add(client.toString(channel) + "(API v." + vi.getApiVersion() + " / BCDice v." + vi.getDiceVersion() + ")");
				return resultList;
			} catch (IOException e) {
				resultList.add(client.toString(channel) + "(バージョン情報の取得に失敗しました)");
				return resultList;
			}
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
			resultList.addAll(client.getDiceUrlList());
			return resultList;
		}
		if(command[3].equals("removeServer")) {
			if(command.length < 5) {
				resultList.add("URL が足りません");
				resultList.add(HELP_ADMIN);
				return resultList;
			} else {
				try {
					boolean removeResult = client.removeDiceServer(command[4]);
					if(removeResult) {
						resultList.add(String.format("%s をダイスサーバのリストから削除しました", command[4]));
					} else {
						resultList.add(String.format("%s がダイスサーバのリストに見つかりませんでした", command[4]));
					}
				} catch(IOException e) {
					resultList.add(e.getMessage());
				}
				return resultList;
			}
		}
		if(command[3].equals("setServer")) {
			if(command.length < 5) {
				resultList.add("URL が足りません");
				resultList.add(HELP_ADMIN);
				return resultList;
			} else {
				client.setDiceServer(command[4]);
				try {
					VersionInfo vi = client.getVersion();
					String msg = client.toString() + "(API v." + vi.getApiVersion() + " / BCDice v." + vi.getDiceVersion() + ")";
					if(msg.contains(command[4])) {
						resultList.add("ダイスサーバを再設定しました");
					} else {
						resultList.add("ダイスサーバの設定に失敗しました。以下のサーバを利用します");
					}
					resultList.add(msg);
				} catch(IOException e) {
					resultList.add(client.toString() + "(ダイスサーバの情報の取得に失敗しました)");
				}
				return resultList;
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
			return separateStringWithLengthLimitation(resultList, 1000);
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
			try {
				originalDiceBotClient.unregisterDiceBot(command[4]);
				resultList.add(String.format("ダイスボット表 [%s] を削除しました", command[4]));
				return resultList;
			} catch(IOException e) {
				logger.warn("ダイスボット表の削除に失敗しました", e);
				resultList.add(e.getMessage());
				return resultList;
			}
		}
		if(command[3].equals("listDiceBot")) {
			List<String> dicebotList = originalDiceBotClient.getDiceBotList();
			resultList.addAll(separateStringWithLengthLimitation(dicebotList, 1000));
			return resultList;
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
	private int saveMessage(String id, String message) {
		List<String> msgList = savedMessage.get(id);
		if(msgList == null) {
			msgList = new ArrayList<String>();
			savedMessage.put(id, msgList);
		}
		msgList.add(message);
		return msgList.size();
	}

	/**
	 * 
	 * @param id user unique id
	 * @param index the called message ID
	 * @return the stacked message
	 * @throws IOException When failed to get message
	 */
	private String getMessage(String id, int index) throws IOException {
		try {
			List<String> list = savedMessage.get(id);
			return list.get(index - 1);
		} catch (Exception e) {
			throw new IOException(e.getMessage(), e);
		}
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

	public static void main(String[] args) {
		BCDiceCLI cli = new BCDiceCLI(args[0].trim(), new OriginalDiceBotClient());
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
