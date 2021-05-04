package com.hiyoko.discord.bot.BCDice;

import org.apache.commons.lang3.RandomStringUtils;

public class AdminPasswordGenerator {
	/**
	 * 管理用パスワードを取得する。
	 * <p>
	 * 環境変数BCDICE_PASSWORDが設定されていれば、その値を返す。
	 * 未設定の場合は、ランダムな文字列を返す。
	 *
	 * @return 管理用パスワード。
	 */
	public static String getPassword() {
		String env = System.getenv("BCDICE_PASSWORD");
		if (env == null) {
			String password = RandomStringUtils.randomAscii(16);
			System.out.println("Admin Password: " + password);
			return password;
		} else {
			System.out.println("Admin Password is written in environment variable BCDICE_PASSWORD");
			return env;
		}
	}
}
