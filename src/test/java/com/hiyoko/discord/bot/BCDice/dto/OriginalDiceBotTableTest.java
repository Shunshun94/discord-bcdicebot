package com.hiyoko.discord.bot.BCDice.dto;

import java.io.IOException;
import java.util.List;

import com.hiyoko.discord.bot.BCDice.OriginalDiceBotClients.OriginalDiceBotClient;
import junit.framework.TestCase;


public class OriginalDiceBotTableTest extends TestCase {
	public void testOriginalDiceBotTableValidation() throws IOException {
		OriginalDiceBotClient client = new OriginalDiceBotClient("./testDiceBots");
		OriginalDiceBotTable a = client.getDiceBot("サンプルダイスボット-ラーメン表");
		assertFalse(a.isValid);
		OriginalDiceBotTable b = client.getDiceBot("サンプルダイスボット-長過ぎる表");
		assertFalse(b.isValid);
		OriginalDiceBotTable c = client.getDiceBot("サンプルダイスボット-夜食表");
		assertTrue(c.isValid);
	}

	public void testOriginalDiceBotTableGetDiceBotList() throws IOException {
		OriginalDiceBotClient client = new OriginalDiceBotClient("./testDiceBots");
		OriginalDiceBotTable nekonbu = client.getDiceBot("nekonbu");
		assertEquals(nekonbu.getName(), "nekonbu");
		OriginalDiceBotTable neko = client.getDiceBot("neko");
		assertEquals(neko.getName(), "neko");
	}

	public void testgetResultsAsInvalidTable() throws IOException {
		OriginalDiceBotClient client = new OriginalDiceBotClient("./testDiceBots");
		OriginalDiceBotTable nekonbu = client.getDiceBot("サンプルダイスボット-夜食表");
		assertEquals(nekonbu.getName(), "サンプルダイスボット-夜食表");
		String input = "#1\n" + 
				"RuinBreakers: (1D12) ＞ 1\n" + 
				"\n" + 
				"#2\n" + 
				"RuinBreakers: (1D12) ＞ 2\n" + 
				"\n" + 
				"#3\n" + 
				"RuinBreakers: (1D12) ＞ 3\n" + 
				"\n" + 
				"#4\n" + 
				"RuinBreakers: (1D12) ＞ 4\n" + 
				"\n" + 
				"#5\n" + 
				"RuinBreakers: (1D12) ＞ 5\n" + 
				"\n" + 
				"#6\n" + 
				"RuinBreakers: (1D12) ＞ 6\n" + 
				"\n" + 
				"#7\n" + 
				"RuinBreakers: (1D12) ＞ 7\n" + 
				"\n" + 
				"#8\n" + 
				"RuinBreakers: (1D12) ＞ 8\n" + 
				"\n" + 
				"#9\n" + 
				"RuinBreakers: (1D12) ＞ 9\n" + 
				"\n" + 
				"#10\n" + 
				"RuinBreakers: (1D12) ＞ 10\n" + 
				"\n" + 
				"#11\n" + 
				"RuinBreakers: (1D12) ＞ 11\n" + 
				"\n" + 
				"#12\n" + 
				"RuinBreakers: (1D12) ＞ 12";
		List<String> target = nekonbu.getResultsAsInvalidTable(input);
		assertEquals(12, target.size());
		assertEquals("#1\nサンプルダイスボット-夜食表(1) ＞ 24時間営業のレストランまでいく", target.get(0));
		assertEquals("#12\nサンプルダイスボット-夜食表(12) ＞ 何も食べない", target.get(11));
		assertEquals(
				nekonbu.getResultAsInvalidTable("RuinBreakers: (1D12) ＞ 1"),
				nekonbu.getResultsAsInvalidTable("RuinBreakers: (1D12) ＞ 1").get(0));
		assertEquals(
				nekonbu.getResultAsInvalidTable("#1\nRuinBreakers: (1D12) ＞ 1"),
				nekonbu.getResultsAsInvalidTable("#1\nRuinBreakers: (1D12) ＞ 1").get(0));

		assertEquals(
				nekonbu.getResultAsInvalidTable("#1\nRuinBreakers: (1D12) ＞ 1\n"),
				nekonbu.getResultsAsInvalidTable("#1\nRuinBreakers: (1D12) ＞ 1\n").get(0));
	}
}
