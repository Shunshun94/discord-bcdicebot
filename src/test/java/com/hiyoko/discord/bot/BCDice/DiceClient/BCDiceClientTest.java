package com.hiyoko.discord.bot.BCDice.DiceClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
}
