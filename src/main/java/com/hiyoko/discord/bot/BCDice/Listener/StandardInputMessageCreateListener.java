package com.hiyoko.discord.bot.BCDice.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.MessageAttachment;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hiyoko.discord.bot.BCDice.BCDiceCLI;
import com.hiyoko.discord.bot.BCDice.ChatTool.ChatToolClient;
import com.hiyoko.discord.bot.BCDice.ChatTool.ChatToolClientFactory;
import com.hiyoko.discord.bot.BCDice.DiceResultFormatter.DiceResultFormatter;
import com.hiyoko.discord.bot.BCDice.DiceResultFormatter.DiceResultFormatterFactory;
import com.hiyoko.discord.bot.BCDice.NameIndicator.NameIndicator;
import com.hiyoko.discord.bot.BCDice.NameIndicator.NameIndicatorFactory;
import com.hiyoko.discord.bot.BCDice.UserSuspender.UserSuspenderFactory;
import com.hiyoko.discord.bot.BCDice.dto.DicerollResult;

public class StandardInputMessageCreateListener implements MessageCreateListener {
	final Logger logger = LoggerFactory.getLogger(StandardInputMessageCreateListener.class);
	final DiscordApi api;
	final BCDiceCLI bcDice;
	final NameIndicator nameIndicator;
	final DiceResultFormatter diceResultFormatter;
	final ChatToolClient chatToolClient;

	public StandardInputMessageCreateListener(DiscordApi api, BCDiceCLI bcDice) {
		this.api = api;
		this.bcDice = bcDice;
		this.nameIndicator = NameIndicatorFactory.getNameIndicator();
		this.diceResultFormatter = DiceResultFormatterFactory.getDiceResultFormatter();
		this.chatToolClient = ChatToolClientFactory.getChatToolClient(api);
		UserSuspenderFactory.initializeUserSuepnder(api.getYourself().getIdAsString());
	}

	@Override
	public void onMessageCreate(MessageCreateEvent event) {
		String channel = event.getChannel().getIdAsString();
		MessageAuthor user = event.getMessageAuthor();
		String name = nameIndicator.getName(user);
		String userId = user.getIdAsString();
		String message = event.getMessage().getContent();

		List<MessageAttachment> attachements = event.getMessage().getAttachments();
		try {
			logger.debug(String.format("%s posts: https://discordapp.com/channels/%s/%s/%s",
					userId,
					event.getServer().get().getIdAsString(), channel, event.getMessage().getIdAsString()));
		} catch(NoSuchElementException e) {
			logger.debug(String.format("%s posts in DM", userId));
		}
		
		api.updateActivity("bcdice help とチャットに打ち込むとコマンドのヘルプを確認できます");
		if( UserSuspenderFactory.getUserSuspender().isSuspended(userId) ) { return; }
		if(chatToolClient.isRequest( message )) {
			List<String> result = chatToolClient.input(message);
			if(! result.isEmpty()) {
				bcDice.separateStringWithLengthLimitation(result, 1000).forEach(p->event.getChannel().sendMessage(p));
				return;
			}
		}
		if(! bcDice.isRoll( message )) {
			bcDice.inputs(message, userId, channel, attachements).forEach(msg->{
				event.getChannel().sendMessage(chatToolClient.formatMessage(msg));
			});
			return;
		}

		try {
			List<DicerollResult> rollResults = bcDice.rolls(message, channel);
			if(rollResults.size() > 0 && rollResults.get(0).isRolled()) {
				logger.debug("Dice command request for dice server is done");
				List<String> sb = new ArrayList<String>();
				for(DicerollResult rollResult : rollResults) {
					if(rollResult.isError()) {
						throw new IOException(rollResult.getText());
					}
					if( rollResult.isRolled() ) {
						sb.add(diceResultFormatter.getText(rollResult));
					}
				}
				List<String> resultMessage = bcDice.separateStringWithLengthLimitation(String.format("＞%s\n%s", name, sb.stream().collect(Collectors.joining("\n\n"))), 1000); 
				DicerollResult firstOne = rollResults.get(0); 
				if( firstOne.isSecret() ) {
					String index = bcDice.saveMessage(userId, resultMessage);
					event.getChannel().sendMessage(chatToolClient.formatMessage(String.format("＞%s\n%s",
							name,
							diceResultFormatter.getText(new DicerollResult(
								String.format("[Secret Dice] Key: %s", index),
								firstOne.getSystem(),
								true, true
							)))));
					try {
						for(String post : resultMessage) {
							api.getUserById(userId).get().sendMessage(chatToolClient.formatMessage(post));
						}
						api.getUserById(userId).get().sendMessage(String.format("この結果を呼び出すには次のようにしてください。\n> bcdice load %s\nこのコマンドは最短72時間後には無効になります\nその後も必要であればそのままコピー&ペーストするか、スクリーンショットなどで共有してください", index));
					} catch (InterruptedException e) {
						throw new IOException(e.getMessage(), e);
					} catch (ExecutionException e) {
						throw new IOException(e.getMessage(), e);
					}
				} else {
					resultMessage.forEach((post)->{
						event.getChannel().sendMessage(chatToolClient.formatMessage(post));
					});
				}
			}
		} catch (IOException e) {
			event.getChannel().sendMessage(String.format("＞%s\n[ERROR]%s", name, e.getMessage()));
			logger.warn(String.format("USERID: %s MESSAGE: %s", userId, message));
			logger.warn("Failed to reply to user request", e);
		}
	}

}
