package com.hiyoko.discord.bot.BCDice.UserSuspender;

import java.util.HashMap;
import java.util.Map;

public class UserSuspender {
	private Map<String, SuspendedUserInfo> suspendedList;
	private final String me;
	public UserSuspender(String botId) {
		me = botId;
		suspendedList = new HashMap<String, SuspendedUserInfo>();
		suspendedList.put(me, new SuspendedUserInfo(me, "Bot 自身であるため"));
	}

	public String putSuspendUser(String userId, String reason) {
		suspendedList.put(userId, new SuspendedUserInfo(userId, reason));
		return String.format("%sをロール抑止ユーザに追加し、ダイスを振れないようにしました。 理由：%s", userId, reason);
	}

	public boolean isSuspended(String userId) {
		return suspendedList.containsKey(userId);
	}

	public SuspendedUserInfo getSuspendedUserInfo(String userId) {
		return suspendedList.get(userId);
	}

	public String removeSuspendedUser(String userId) {
		if(userId.equals(me)) {
			return "Bot自身をロール抑止ユーザから除くことはできません";
		}
		if(isSuspended(userId)) {
			suspendedList.remove(userId);
			return String.format("%sをロール抑止ユーザから削除し、再度ダイスを振れるようにしました", userId);
		} else {
			return String.format("%sはロール抑止ユーザに含まれていません", userId);
		}
	}
}
