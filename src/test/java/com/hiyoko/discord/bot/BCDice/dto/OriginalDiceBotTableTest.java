package com.hiyoko.discord.bot.BCDice.dto;

import java.io.IOException;

import com.hiyoko.discord.bot.BCDice.OriginalDiceBotClients.OriginalDiceBotClient;
import junit.framework.TestCase;


public class OriginalDiceBotTableTest extends TestCase {
	// 
	public void testOriginalDiceBotTableValidation() throws IOException {
		OriginalDiceBotClient client = new OriginalDiceBotClient("./testDiceBots");
		OriginalDiceBotTable a = client.getDiceBot("サンプルダイスボット-ラーメン表");
		assertFalse(a.isValid);
		OriginalDiceBotTable b = client.getDiceBot("サンプルダイスボット-長過ぎる表");
		assertFalse(b.isValid);
		OriginalDiceBotTable c = client.getDiceBot("サンプルダイスボット-夜食表");
		assertTrue(c.isValid);
	}
}
