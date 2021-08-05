package com.hiyoko.discord.bot.BCDice.DiceResultFormatter;

import java.io.IOException;

import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;

import junit.framework.TestCase;

public class AsV1DiceResultFormatterTest extends TestCase {
	public void testMultiLine() {
		AsV1DiceResultFormatter formatter = new AsV1DiceResultFormatter();
		DicerollResult multLine1 = new DicerollResult(
			"#a\n" + 
			"狂気の発作（リアルタイム）(9) ＞ 恐怖症：探索者は新しい恐怖症に陥る。恐怖症表（PHコマンド）をロールするか、キーパーが恐怖症を1つ選ぶ。恐怖症の原因は存在しなくとも、その探索者は次の1D10ラウンドの間、それがそこにあると思い込む。(1D10＞8ラウンド)\n" + 
			"\n" + 
			"# b b\n" + 
			"狂気の発作（リアルタイム）(10) ＞ マニア：探索者は新しいマニアに陥る。マニア表（MAコマンド）をロールするか、キーパーがマニアを1つ選ぶ。その探索者は次の1D10ラウンドの間、自分の新しいマニアに没頭しようとする。(1D10＞2ラウンド)\n" + 
			"\n" + 
			"# c\n" + 
			"狂気の発作（リアルタイム）(4) ＞ 偏執症：探索者は1D10ラウンドの間、重い偏執症に襲われる。誰もが探索者に襲い掛かろうとしている。信用できる者はいない。監視されている。裏切ったやつがいる。これはわなだ。(1D10＞8ラウンド)",
			"Cthulhu7th", false, true);
		assertEquals(
				"#a\n" + 
				"Cthulhu7th: 狂気の発作（リアルタイム）(9) ＞ 恐怖症：探索者は新しい恐怖症に陥る。恐怖症表（PHコマンド）をロールするか、キーパーが恐怖症を1つ選ぶ。恐怖症の原因は存在しなくとも、その探索者は次の1D10ラウンドの間、それがそこにあると思い込む。(1D10＞8ラウンド)\n" + 
				"\n" + 
				"# b b\n" + 
				"Cthulhu7th: 狂気の発作（リアルタイム）(10) ＞ マニア：探索者は新しいマニアに陥る。マニア表（MAコマンド）をロールするか、キーパーがマニアを1つ選ぶ。その探索者は次の1D10ラウンドの間、自分の新しいマニアに没頭しようとする。(1D10＞2ラウンド)\n" + 
				"\n" + 
				"# c\n" + 
				"Cthulhu7th: 狂気の発作（リアルタイム）(4) ＞ 偏執症：探索者は1D10ラウンドの間、重い偏執症に襲われる。誰もが探索者に襲い掛かろうとしている。信用できる者はいない。監視されている。裏切ったやつがいる。これはわなだ。(1D10＞8ラウンド)",
				formatter.getText(multLine1));

		DicerollResult multLine2 = new DicerollResult(
				"#1\n" + 
				"(2D6) ＞ 7[2,5] ＞ 7\n" + 
				"\n" + 
				"#2\n" + 
				"(2D6) ＞ 6[5,1] ＞ 6\n" + 
				"\n" + 
				"#3\n" + 
				"(2D6) ＞ 6[1,5] ＞ 6",
				"DiceBot", false, true);
		assertEquals(
				"#1\n" + 
				"DiceBot: (2D6) ＞ 7[2,5] ＞ 7\n" + 
				"\n" + 
				"#2\n" + 
				"DiceBot: (2D6) ＞ 6[5,1] ＞ 6\n" + 
				"\n" + 
				"#3\n" + 
				"DiceBot: (2D6) ＞ 6[1,5] ＞ 6",
				formatter.getText(multLine2));

		DicerollResult multLine3 = new DicerollResult(
				"#a\n" + 
				"#1\n" + 
				"(2D6) ＞ 5[4,1] ＞ 5\n" + 
				"\n" + 
				"#2\n" + 
				"(2D6) ＞ 8[2,6] ＞ 8\n" + 
				"\n" + 
				"#b\n" + 
				"#1\n" + 
				"(2D6) ＞ 7[2,5] ＞ 7\n" + 
				"\n" + 
				"#2\n" + 
				"(2D6) ＞ 5[2,3] ＞ 5\n" + 
				"\n" + 
				"#c\n" + 
				"#1\n" + 
				"(2D6) ＞ 10[5,5] ＞ 10\n" + 
				"\n" + 
				"#2\n" + 
				"(2D6) ＞ 5[1,4] ＞ 5",
				"DiceBot", false, true);
		assertEquals(
				"#a\n" + 
				"#1\n" + 
				"DiceBot: (2D6) ＞ 5[4,1] ＞ 5\n" + 
				"\n" + 
				"#2\n" + 
				"DiceBot: (2D6) ＞ 8[2,6] ＞ 8\n" + 
				"\n" + 
				"#b\n" + 
				"#1\n" + 
				"DiceBot: (2D6) ＞ 7[2,5] ＞ 7\n" + 
				"\n" + 
				"#2\n" + 
				"DiceBot: (2D6) ＞ 5[2,3] ＞ 5\n" + 
				"\n" + 
				"#c\n" + 
				"#1\n" + 
				"DiceBot: (2D6) ＞ 10[5,5] ＞ 10\n" + 
				"\n" + 
				"#2\n" + 
				"DiceBot: (2D6) ＞ 5[1,4] ＞ 5",
				formatter.getText(multLine3));
	}

	public void testSingleLine() throws IOException {
		AsV1DiceResultFormatter formatter = new AsV1DiceResultFormatter();
		DicerollResult commonRole = new DicerollResult("{\"ok\":true,\"text\":\"KeyNo.20c[10] ＞ 2D:[3,4]=7 ＞ 5\",\"secret\":false,\"success\":false,\"failure\":false,\"critical\":false,\"fumble\":false,\"rands\":[{\"kind\":\"normal\",\"sides\":6,\"value\":3},{\"kind\":\"normal\",\"sides\":6,\"value\":4}]}", "SwordWorld2.5");
		assertEquals("SwordWorld2.5: KeyNo.20c[10] ＞ 2D:[3,4]=7 ＞ 5", formatter.getText(commonRole));
		DicerollResult tableRoll = new DicerollResult("{\"ok\":true,\"text\":\"飲み物表(6) ＞ 選ばれし者の知的飲料\",  \"rands\":[{\"kind\":\"normal\",\"sides\":6,\"value\":6}]}");
		assertEquals("DiceBot: 飲み物表(6) ＞ 選ばれし者の知的飲料", formatter.getText(tableRoll));
	}
}
