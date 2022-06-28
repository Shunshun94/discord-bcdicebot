package com.hiyoko.discord.bot.BCDice.AdminCommand;

import java.util.HashMap;
import java.util.Map;

public class AdminCommandsMapFactory {
	public static Map<String, AdminCommand> getAdminCommands() {
		Map<String, AdminCommand> adminCommands = new HashMap<String, AdminCommand>();
		adminCommands.put("setserver", new SetServer());
		adminCommands.put("removeserver", new RemoveServer());
		adminCommands.put("listserver", new ListServer());
		adminCommands.put("listoriginaltable", new ListOriginalTable());
		adminCommands.put("addoriginaltable", new AddOriginalTable());
		adminCommands.put("removeoriginaltable", new RemoveOriginalTable());
		adminCommands.put("refreshsecretdice", new RefreshSecretDice());
		adminCommands.put("updatedicerollprefix", new UpdateDiceRollPreFix());
		return adminCommands;
	}
}
