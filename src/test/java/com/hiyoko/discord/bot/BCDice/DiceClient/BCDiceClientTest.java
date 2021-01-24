package com.hiyoko.discord.bot.BCDice.DiceClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class BCDiceClientTest extends TestCase {
	public void testIsDiceCommand() {
		BCDiceClient client = new BCDiceClient("");
		try (BufferedReader br = Files.newBufferedReader(Paths.get("src/test/resources/shouldRolled.txt"))) {
			String line;
			while( (line = br.readLine()) != null ) {
				if( ! line.isEmpty() ) {
					if(! client.isDiceCommand(line)) {
						throw new RuntimeException(String.format("%s should be rolled but not rolled.", line));
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void testDiceSecondary() throws IOException {
		// Dummy servers
		String urlA = "http://hiyo-hitsu.sakura.ne.jp/returnCode.cgi?statusCode=500&path=";
		String urlB = "http://hiyo-hitsu.sakura.ne.jp/returnCode.cgi?statusCode=500&a=b&path=";
		String urlC = "http://hiyo-hitsu.sakura.ne.jp/returnCode.cgi?statusCode=200&ok=true&result=8&secret=false&path=";
		BCDiceClient client = new BCDiceClient(urlA);
		assertEquals(String.format("%s/", urlA), client.getDiceUrlList().get(client.getUrlCursor()));
		client.setDiceServer(urlB);
		assertEquals(2, client.getDiceUrlList().size());
		assertEquals(String.format("%s/", urlB), client.getDiceUrlList().get(client.getUrlCursor()));
		client.setDiceServer(urlA);
		assertEquals(String.format("%s/", urlA), client.getDiceUrlList().get(client.getUrlCursor()));
		assertEquals(2, client.getDiceUrlList().size());
		client.setDiceServer(urlC);
		assertEquals(String.format("%s/", urlC), client.getDiceUrlList().get(client.getUrlCursor()));
		assertEquals(3, client.getDiceUrlList().size());
		try {
			assertTrue(client.rollDice("2d6").isRolled());
			assertEquals(String.format("%s/", urlC), client.getDiceUrlList().get(client.getUrlCursor()));
			client.setDiceServer(urlB);
			assertTrue(client.rollDice("2d6").isRolled());
			assertEquals(String.format("%s/", urlC), client.getDiceUrlList().get(client.getUrlCursor()));
		} catch (IOException e) {
			System.out.println("Failed");
			e.printStackTrace();
		}
		try {
			assertEquals(2, client.getUrlCursor());
			assertFalse(client.removeDiceServer("http://bcdice.not-exist.example.com"));
			assertTrue(client.removeDiceServer(urlA));
			assertEquals(2, client.getDiceUrlList().size());
			assertEquals(0, client.getUrlCursor());
			assertTrue(client.removeDiceServer(urlB));
			assertEquals(1, client.getDiceUrlList().size());
			assertEquals(0, client.getUrlCursor());
			client.removeDiceServer(urlC);
			throw new RuntimeException("Failed to test testDiceSecondary. All dice servers must not be removed.");
		} catch (IOException e) {
			// NO ACTION. Exception is expected
		}
	}

	public void testPresecondary() {
		String urlA = "http://hiyo-hitsu.sakura.ne.jp/returnCode.cgi?statusCode=500&path=";
		String urlB = "http://hiyo-hitsu.sakura.ne.jp/returnCode.cgi?statusCode=500&a=b&path=";
		List<String> list = new ArrayList<String>();
		list.add(urlA);
		list.add(urlB);
		BCDiceV2Client client = (BCDiceV2Client) DiceClientFactory.getDiceClient(list, true);

		assertEquals(2, client.getDiceUrlList().size());
		assertEquals(String.format("%s/", urlA), client.getDiceUrlList().get(client.getUrlCursor()));
		client.setDiceServer(urlB);
		assertEquals(String.format("%s/", urlB), client.getDiceUrlList().get(client.getUrlCursor()));
		assertEquals(2, client.getDiceUrlList().size());
	}
}
