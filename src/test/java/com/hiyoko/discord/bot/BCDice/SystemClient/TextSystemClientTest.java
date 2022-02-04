package com.hiyoko.discord.bot.BCDice.SystemClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import junit.framework.TestCase;

public class TextSystemClientTest extends TestCase {
	private final SystemClient sc;
	private final Map<String, String> EMPTY_SEED_MAP = new HashMap<String, String>();
	public TextSystemClientTest(String name) throws IOException {
		super(name);
		sc = new TextSystemClient("TextSystemClientTestDirectory");
	}

	@Test
	public void testEmptyFile() throws IOException {
		sc.exportSystemList(EMPTY_SEED_MAP);
		Map<String, String> emptySeedMap = new HashMap<String, String>();

		Map<String, String> emtpyResultFromEmptyFile; 
		emtpyResultFromEmptyFile = sc.getSystemList();
		assertEquals(0, emtpyResultFromEmptyFile.size());

		emptySeedMap.put("someString", "someSystem");
		sc.exportSystemList(emptySeedMap);
		emtpyResultFromEmptyFile = sc.getSystemList();
		assertEquals(0, emtpyResultFromEmptyFile.size());
	}

	@Test
	public void testCommonBehavior() throws IOException {
		sc.exportSystemList(EMPTY_SEED_MAP);
		Map<String, String> fileSeedMap = new HashMap<String, String>();
		Map<String, String> result;

		fileSeedMap.put("someString", "someSystem");
		fileSeedMap.put("001", "systemA");
		fileSeedMap.put("002", "systemB");
		fileSeedMap.put("003", "systemC");
		fileSeedMap.put("004", "systemD");
		fileSeedMap.put("005", "systemE");
		sc.exportSystemList(fileSeedMap);
		result = sc.getSystemList();
		assertEquals(5, result.size());
		assertEquals("systemA", result.get("001"));

		fileSeedMap.remove("001");
		assertEquals(5, result.size());
		assertEquals("systemA", result.get("001"));

		sc.exportSystemList(fileSeedMap);
		result = sc.getSystemList();
		assertEquals(4, result.size());
		assertEquals(null, result.get("001"));
	}
}
