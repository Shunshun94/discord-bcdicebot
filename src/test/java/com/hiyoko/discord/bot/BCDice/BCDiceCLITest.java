package com.hiyoko.discord.bot.BCDice;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;
import java.io.IOException;

import com.hiyoko.discord.bot.BCDice.OriginalDiceBotClients.OriginalDiceBotClient;
import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;

import junit.framework.TestCase;

public class BCDiceCLITest extends TestCase {

	private BCDiceCLI cli;
	private String PASSWORD = "mypassword";

	public BCDiceCLITest(String name) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		super(name);
	    Class<?> clazz = Class.forName("java.lang.ProcessEnvironment");
	    Field theCaseInsensitiveEnvironment = clazz.getDeclaredField("theCaseInsensitiveEnvironment");
	    theCaseInsensitiveEnvironment.setAccessible(true);
	    @SuppressWarnings("unchecked")
		Map<String,String> sytemEnviroment = (Map<String, String>) theCaseInsensitiveEnvironment.get(null);
	    sytemEnviroment.put("BCDICE_PASSWORD", PASSWORD);
		
		cli = new BCDiceCLI("mock", new OriginalDiceBotClient("./testDiceBots"));
	}
	
	public void testIsRoll() {
		assertFalse(cli.isRoll("bcdice hiyoko"));
		assertFalse(cli.isRoll("BCDice hiyoko"));
		assertFalse(cli.isRoll("BCDice"));
		assertTrue(cli.isRoll("bcdiceだよ"));
		assertTrue(cli.isRoll("2d6"));
		assertTrue(cli.isRoll("koneko"));
	}

	public void testRoll() {
		try {
			String system = "kindness";
			cli.inputs("bcdice set " + system, "", "neko");
			DicerollResult dr = cli.roll("2d6", "neko");
			assertEquals(dr.getSystem(), system);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	public void testInputStringHelp() {
		assertEquals(cli.inputs("bcdice help", "", "channel").get(0) , BCDiceCLI.HELP);
		assertEquals(cli.inputs("bcdice", "", "channel").get(0), BCDiceCLI.HELP);
		assertEquals(cli.inputs("bcdice nonsense", "", "channel").get(0), BCDiceCLI.HELP);
		List<String> diceBotList = cli.inputs("bcdice list", "dummy", "dummy");
		assertTrue(cli.inputs("bcdice help nonsense", "", "channel").get(0).indexOf("is not found") > -1);
		assertTrue(cli.inputs("bcdice help " + diceBotList.get(1), "", "channel").get(0).indexOf("is not found") == -1);
		assertTrue(cli.inputs("bcdice help " + diceBotList.get(1) + "\ndayodayo", "", "channel").get(0).indexOf("is not found") == -1);
	}
	
	public void testInputStringSet() {
		List<String> diceBotList = cli.inputs("bcdice list", "dummy", "dummy");
		assertTrue(cli.inputs("bcdice set", "", "channel").get(0).indexOf("ERROR") > -1);
		assertTrue(cli.inputs("bcdice set " + diceBotList.get(1), "", "channel").get(0).indexOf("ERROR") == -1);
		assertTrue(cli.inputs("bcdice set " + diceBotList.get(1) + " nonsense", "", "channel").get(0).indexOf("ERROR") == -1);
		assertTrue(cli.inputs("bcdice set " + diceBotList.get(1) + "\nhiyohiyo", "", "channel").get(0).indexOf("ERROR") == -1);
		assertTrue(cli.inputs("bcdice set Hiyoko", "", "hiyohitsu").get(0).contains("Hiyoko"));
		assertTrue(cli.inputs("bcdice set hitsuji & hiyoko", "", "hiyohitsu").get(0).contains("hitsuji & hiyoko"));
	}

	public void testInputStringStack() {
		String[] list = {"hiyoko", "hiyoko hitsuji", "hiyoko\nhitsuji", "hiyoko hitsuji\nkoneko\nkoinu"};
		try {
			assertEquals(cli.inputs("bcdice save " + list[0], "hiyoko", "channel").get(0), "1");
			assertEquals(cli.inputs("bcdice save " + list[1], "hiyoko", "channel").get(0), "2");
			assertEquals(cli.inputs("bcdice save " + list[2], "hiyoko", "channel").get(0), "3");
			assertEquals(cli.inputs("bcdice save " + list[3], "hiyoko", "channel").get(0), "4");
			assertEquals(cli.inputs("bcdice save", "hiyoko", "channel").get(0), "5");
			
			assertEquals(cli.inputs("bcdice load 1", "hiyoko", "channel").get(0), list[0]);
			assertEquals(cli.inputs("bcdice load 2", "hiyoko", "channel").get(0), list[1]);
			assertEquals(cli.inputs("bcdice load 3", "hiyoko", "channel").get(0), list[2]);
			assertEquals(cli.inputs("bcdice load 4", "hiyoko", "channel").get(0), list[3]);
			assertEquals(cli.inputs("bcdice load 5", "hiyoko", "channel").get(0), "");
			assertTrue(cli.inputs("bcdice load 1", "", "channel").get(0).startsWith("Not found"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testInputStringString() {
		assertEquals(cli.inputs("bcdice status", "", "channel").get(0), cli.inputs("bcdice status", "koneko", "channel").get(0));
	}
	
	public void testMultiChannel() {
		List<String> tmpDiceBotList = cli.inputs("bcdice list", "", "channel");
		List<String> diceBotList = Arrays.asList(String.join("\n", tmpDiceBotList).split("\n"));
		cli.inputs("bcdice set " + diceBotList.get(2) , "hiyoko", "ungeneral");
		assertEquals(cli.inputs("bcdice status", "hiyoko", "general").get(0), cli.inputs("bcdice status", "hiyoko", "general").get(0));

		assertTrue(cli.inputs("bcdice status", "hiyoko", "ungeneral").get(0).contains(diceBotList.get(2)));
		assertFalse(cli.inputs("bcdice status", "hiyoko", "general").get(0).contains(diceBotList.get(2)));
		assertFalse(cli.inputs("bcdice status", "hiyoko", "ungeneral").get(0).contains(diceBotList.get(1)));
		cli.inputs("bcdice set " + diceBotList.get(3), "hiyoko", "ungeneral");
		assertTrue(cli.inputs("bcdice status", "hiyoko", "ungeneral").get(0).contains(diceBotList.get(3)));
		assertFalse(cli.inputs("bcdice status", "hiyoko", "general").get(0).contains(diceBotList.get(3)));
		assertTrue(cli.inputs("bcdice status", "hiyoko", "dummydummy").get(0).contains(diceBotList.get(1)));
	}

	public void testNormalizeCommand() throws IOException {
		// From https://github.com/Shunshun94/discord-bcdicebot/pull/10#issuecomment-374023404
		String acctualText = cli.roll("2d6 <= 8 / ああああaaa[~'()&?!]", "nonChannel").getText();
		String expectedText = "2d6%3C%3D8%20%2F%20%E3%81%82%E3%81%82%E3%81%82%E3%81%82aaa%5B~%27%28%29%26%3F%21%5D";
		assertEquals(expectedText, acctualText);
		assertEquals("1d10%3C5", cli.roll("1d10 < 5", "nonChannel").getText());
		assertEquals("1d10%3E5", cli.roll("1d10 > 5", "nonChannel").getText());
		assertEquals("2d6aa%20a%3Cbb%20b%3Dc%20cc%3Edd%20d%3C%3D%3E%3D%3D%3C%3D%3Edd%20d", cli.roll("2d6aa a < bb b = c cc > dd d <= >=  =< => dd d", "nonChannel").getText());
	}

	public void testAdmin() {
		assertTrue(cli.inputs("bcdice admin InvalidPassword help", "", "channel").get(0).contains("パスワードが違います"));
		assertEquals(cli.inputs("bcdice admin " + PASSWORD + " help", "", "channel").get(0), BCDiceCLI.HELP_ADMIN);
	}

	public void testSupressionMode() throws IOException {
		String PREFIX = "/hiyoko";
		assertTrue(cli.inputs("bcdice admin " + PASSWORD + " suppressroll", "", "channel").get(0).contains("まずコマンドじゃないだろう"));
		assertTrue(cli.roll("サンプルダイスボット-夜食表", "no_channel").isRolled());
		assertFalse(cli.roll(PREFIX + " サンプルダイスボット-夜食表", "no_channel").isRolled());
		assertTrue(cli.roll("2d6", "channel").isRolled());
		assertFalse(cli.roll(PREFIX + " 2d6", "channel").isRolled());
		assertFalse(cli.roll("あああああ", "channel").isRolled());
		assertFalse(cli.roll(PREFIX + " あああああ", "channel").isRolled());

		assertTrue(cli.inputs("bcdice admin " + PASSWORD + " suppressroll disable", "", "channel").get(0).contains("すべてのコマンドがサーバに送信されます"));
		assertTrue(cli.roll("サンプルダイスボット-夜食表", "no_channel").isRolled());
		assertTrue(cli.roll(PREFIX + " サンプルダイスボット-夜食表", "no_channel").isRolled());
		assertTrue(cli.roll("2d6", "channel").isRolled());
		assertTrue(cli.roll(PREFIX + " 2d6", "channel").isRolled());
		assertTrue(cli.roll("あああああ", "channel").isRolled());
		assertTrue(cli.roll(PREFIX + " あああああ", "channel").isRolled());

		assertTrue(cli.inputs("bcdice admin " + PASSWORD + " suppressroll /hiyoko", "", "channel").get(0).contains("で始まるコマンドのみサーバに送信します "));
		assertFalse(cli.roll("サンプルダイスボット-夜食表", "no_channel").isRolled());
		assertTrue(cli.roll(PREFIX + "サンプルダイスボット-夜食表", "no_channel").isRolled());
		assertTrue(cli.roll("サンプルダイスボット-夜食表", "no_channel").getText().isEmpty());
		assertFalse(cli.roll(PREFIX + " サンプルダイスボット-夜食表", "no_channel").getText().isEmpty());
		assertFalse(cli.roll("2d6", "channel").isRolled());
		assertTrue(cli.roll(PREFIX + " 2d6", "channel").isRolled());
		assertFalse(cli.roll("あああああ", "channel").isRolled());
		assertTrue(cli.roll(PREFIX + " あああああ", "channel").isRolled());

		assertTrue(cli.inputs("bcdice admin " + PASSWORD + " suppressroll", "", "channel").get(0).contains("まずコマンドじゃないだろう"));
		assertTrue(cli.roll("サンプルダイスボット-夜食表", "no_channel").isRolled());
		assertFalse(cli.roll(PREFIX + " サンプルダイスボット-夜食表", "no_channel").isRolled());
		assertFalse(cli.roll("サンプルダイスボット-夜食表", "no_channel").getText().isEmpty());
		assertTrue(cli.roll(PREFIX + " サンプルダイスボット-夜食表", "no_channel").getText().isEmpty());
		assertTrue(cli.roll("2d6", "channel").isRolled());
		assertFalse(cli.roll(PREFIX + " 2d6", "channel").isRolled());
		assertFalse(cli.roll("あああああ", "channel").isRolled());
		assertFalse(cli.roll(PREFIX + " あああああ", "channel").isRolled());
		
		assertTrue(cli.inputs("bcdice admin " + PASSWORD + " suppressroll " + PREFIX, "", "channel").get(0).contains("で始まるコマンドのみサーバに送信します "));
		assertFalse(cli.roll("サンプルダイスボット-夜食表", "no_channel").isRolled());
		assertTrue(cli.roll(PREFIX + " サンプルダイスボット-夜食表", "no_channel").isRolled());
		assertTrue(cli.roll("サンプルダイスボット-夜食表", "no_channel").getText().isEmpty());
		assertFalse(cli.roll(PREFIX + " サンプルダイスボット-夜食表", "no_channel").getText().isEmpty());
		assertFalse(cli.roll("2d6", "channel").isRolled());
		assertTrue(cli.roll(PREFIX + " 2d6", "channel").isRolled());
		assertFalse(cli.roll("あああああ", "channel").isRolled());
		assertTrue(cli.roll(PREFIX + " あああああ", "channel").isRolled());
		
		assertTrue(cli.inputs("bcdice admin " + PASSWORD + " suppressroll disable", "", "channel").get(0).contains("すべてのコマンドがサーバに送信されます"));
		assertTrue(cli.roll("サンプルダイスボット-夜食表", "no_channel").isRolled());
		assertTrue(cli.roll(PREFIX + " サンプルダイスボット-夜食表", "no_channel").isRolled());
		assertFalse(cli.roll("サンプルダイスボット-夜食表", "no_channel").getText().isEmpty());
		assertTrue(cli.roll("2d6", "channel").isRolled());
		assertTrue(cli.roll(PREFIX + " 2d6", "channel").isRolled());
		assertTrue(cli.roll("あああああ", "channel").isRolled());
		assertTrue(cli.roll(PREFIX + " あああああ", "channel").isRolled());

		assertTrue(cli.inputs("bcdice admin " + PASSWORD + " suppressroll", "", "channel").get(0).contains("まずコマンドじゃないだろう"));
	}

	public void testOriginalDiceBot() throws IOException {
		assertTrue(cli.roll("サンプルダイスボット-夜食表", "no_channel").isRolled());
	}

	public void testMultiroll() throws Exception {
		assertEquals(1, cli.rolls("2d6", "no_channel").size());
		assertEquals(1, cli.rolls("repeat3 2d6", "no_channel").size());
		assertEquals(1, cli.rolls("3 2d6", "no_channel").size());
		assertEquals(3, cli.rolls("[パンダ,うさぎ,コアラ] 2d6", "no_channel").size());
		assertEquals(3, cli.rolls("3 サンプルダイスボット-夜食表", "no_channel").size());
		assertEquals(3, cli.rolls("rep3 サンプルダイスボット-夜食表", "no_channel").size());
		assertEquals(3, cli.rolls("repeat3 サンプルダイスボット-夜食表", "no_channel").size());
		assertEquals(3, cli.rolls("x3 サンプルダイスボット-夜食表", "no_channel").size());
		assertEquals(20, cli.rolls("20 サンプルダイスボット-夜食表", "no_channel").size());
		assertEquals(0, cli.rolls("21 なにもない", "no_channel").size());

		String PREFIX = "/hiyoko";
		assertTrue(cli.inputs("bcdice admin " + PASSWORD + " suppressroll " + PREFIX, "", "channel").get(0).contains("で始まるコマンドのみサーバに送信します "));
		assertEquals(cli.rolls(PREFIX + " 2d6", "no_channel").size(), 1);
		assertEquals(cli.rolls(PREFIX + " 3 2d6", "no_channel").size(), 1);
		assertEquals(cli.rolls(PREFIX + " repeat3 2d6", "no_channel").size(), 1);
		assertEquals(cli.rolls(PREFIX + " [パンダ,うさぎ,コアラ] 2d6", "no_channel").size(), 3);
		assertEquals(cli.rolls(PREFIX + " 3 サンプルダイスボット-夜食表", "no_channel").size(), 3);
		assertEquals(cli.rolls(PREFIX + " x3 サンプルダイスボット-夜食表", "no_channel").size(), 3);
		assertEquals(cli.rolls(PREFIX + " repeat3 サンプルダイスボット-夜食表", "no_channel").size(), 3);
		assertEquals(cli.rolls(PREFIX + " rep3 サンプルダイスボット-夜食表", "no_channel").size(), 3);
		assertEquals(cli.rolls(PREFIX + " 20 サンプルダイスボット-夜食表", "no_channel").size(), 20);
		// 間にスペースなし
		assertEquals(cli.rolls(PREFIX + "2d6", "no_channel").size(), 1);
		assertEquals(cli.rolls(PREFIX + "3 2d6", "no_channel").size(), 1);
		assertEquals(cli.rolls(PREFIX + "[パンダ,うさぎ,コアラ] 2d6", "no_channel").size(), 3);
		assertEquals(cli.rolls(PREFIX + "サンプルダイスボット-夜食表", "no_channel").size(), 1);
		assertEquals(cli.rolls(PREFIX + "3 サンプルダイスボット-夜食表", "no_channel").size(), 3);
		assertEquals(cli.rolls(PREFIX + "x3 サンプルダイスボット-夜食表", "no_channel").size(), 3);
		assertEquals(cli.rolls(PREFIX + "rep3 サンプルダイスボット-夜食表", "no_channel").size(), 3);
		assertEquals(cli.rolls(PREFIX + "repeat3 サンプルダイスボット-夜食表", "no_channel").size(), 3);
		assertEquals(cli.rolls(PREFIX + "20 サンプルダイスボット-夜食表", "no_channel").size(), 20);
		// 全角スペース
		assertEquals(cli.rolls(PREFIX + "　2d6", "no_channel").size(), 1);
		assertEquals(cli.rolls(PREFIX + "　3　2d6", "no_channel").size(), 1);
		assertEquals(cli.rolls(PREFIX + "　サンプルダイスボット-夜食表", "no_channel").size(), 1);
		assertEquals(cli.rolls(PREFIX + "　[パンダ,うさぎ,コアラ]　2d6", "no_channel").size(), 3);
		assertEquals(cli.rolls(PREFIX + "　3　サンプルダイスボット-夜食表", "no_channel").size(), 3);
		assertEquals(cli.rolls(PREFIX + "　x3　サンプルダイスボット-夜食表", "no_channel").size(), 3);
		assertEquals(cli.rolls(PREFIX + "　rep3　サンプルダイスボット-夜食表", "no_channel").size(), 3);
		assertEquals(cli.rolls(PREFIX + "　repeat3　サンプルダイスボット-夜食表", "no_channel").size(), 3);
		assertEquals(cli.rolls(PREFIX + "　20　サンプルダイスボット-夜食表", "no_channel").size(), 20);
	}
}
