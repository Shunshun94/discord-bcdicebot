package com.hiyoko.discord.bot.BCDice;

import java.io.IOException;

import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;

import junit.framework.TestCase;

public class BCDiceCLITest extends TestCase {

	private BCDiceCLI cli;
	
	public BCDiceCLITest(String name){
		super(name);
		cli = new BCDiceCLI("mock");
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
			cli.input("bcdice set " + system);
			DicerollResult dr = cli.roll("2d6");
			assertEquals(dr.getSystem(), system);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	public void testInputStringHelp() {
		assertEquals(cli.input("bcdice help"), BCDiceCLI.HELP);
		assertEquals(cli.input("bcdice"), BCDiceCLI.HELP);
		assertEquals(cli.input("bcdice nonsense"), BCDiceCLI.HELP);
		String[] diceBotList = cli.input("bcdice list").split("\n");
		assertTrue(cli.input("bcdice help nonsense").indexOf("is not found") > -1);
		assertTrue(cli.input("bcdice help " + diceBotList[1]).indexOf("is not found") == -1);
		assertTrue(cli.input("bcdice help " + diceBotList[1] + "\ndayodayo").indexOf("is not found") == -1);
	}
	
	public void testInputStringSet() {
		String[] diceBotList = cli.input("bcdice list").split("\n");
		assertTrue(cli.input("bcdice set").indexOf("ERROR") > -1);
		assertTrue(cli.input("bcdice set " + diceBotList[1]).indexOf("ERROR") == -1);
		assertTrue(cli.input("bcdice set " + diceBotList[1] + " nonsense").indexOf("ERROR") == -1);
		assertTrue(cli.input("bcdice set " + diceBotList[1] + "\nhiyohiyo").indexOf("ERROR") == -1);
	}

	public void testInputStringStack() {
		String[] list = {"hiyoko", "hiyoko hitsuji", "hiyoko\nhitsuji", "hiyoko hitsuji\nkoneko\nkoinu"};
		try {
			assertEquals(cli.input("bcdice save " + list[0], "hiyoko"), "1");
			assertEquals(cli.input("bcdice save " + list[1], "hiyoko"), "2");
			assertEquals(cli.input("bcdice save " + list[2], "hiyoko"), "3");
			assertEquals(cli.input("bcdice save " + list[3], "hiyoko"), "4");
			assertEquals(cli.input("bcdice save", "hiyoko"), "5");
			
			assertEquals(cli.input("bcdice load 1", "hiyoko"), list[0]);
			assertEquals(cli.input("bcdice load 2", "hiyoko"), list[1]);
			assertEquals(cli.input("bcdice load 3", "hiyoko"), list[2]);
			assertEquals(cli.input("bcdice load 4", "hiyoko"), list[3]);
			assertEquals(cli.input("bcdice load 5", "hiyoko"), "");
			assertTrue(cli.input("bcdice load 1").startsWith("Not found"));
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	public void testInputStringString() {
		assertEquals(cli.input("bcdice status"), cli.input("bcdice status", "koneko"));
	}
	
	public void testMultiChannel() {
		String[] diceBotList = cli.input("bcdice list").split("\n");
		cli.input("bcdice set " + diceBotList[1]);
		assertEquals(cli.input("bcdice status", "hiyoko"), cli.input("bcdice status", "hiyoko", "general"));
		assertEquals(cli.input("bcdice status", "hiyoko"), cli.input("bcdice status", "hiyoko", "ungeneral"));
		assertTrue(cli.input("bcdice status", "hiyoko", "ungeneral").indexOf(diceBotList[1]) != -1);
		cli.input("bcdice set " + diceBotList[2], "hiyoko", "ungeneral");
		assertTrue(cli.input("bcdice status", "hiyoko", "ungeneral").indexOf(diceBotList[2]) != -1);
		assertTrue(cli.input("bcdice status", "hiyoko", "general").indexOf(diceBotList[1]) != -1);
		assertTrue(cli.input("bcdice status", "hiyoko").indexOf(diceBotList[1]) != -1);
	}

	public void testNormalizeCommand() throws IOException {
		// From https://github.com/Shunshun94/discord-bcdicebot/pull/10#issuecomment-374023404
		assertEquals(cli.roll("1d100 < = 50").getText(), "1d100%20<%20=%2050");
		assertEquals(cli.roll("1d100 < = 50 聞き耳").getText(), "1d100%20<%20=%2050%20聞き耳");
		assertEquals(cli.roll("2d6+3 2 度目の攻撃！").getText(), "2d6+3%202%20度目の攻撃！");
		assertEquals(cli.roll("2d6 + 3 2 度目の攻撃！").getText(), "2d6%20+%203%202%20度目の攻撃！");
		assertEquals(cli.roll("3JD4+ おりゃー").getText(), "3JD4+%20おりゃー");
	}
}
