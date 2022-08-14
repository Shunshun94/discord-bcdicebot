package com.hiyoko.discord.bot.BCDice.dto;

import java.io.IOException;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.ParseException;

public class DicerollResult {
	private final String text;
	private final boolean secret;
	private final boolean rolled;
	private final boolean isError;
	private final String system;

	private final String UNSUPPORTED_DICEBOT = "unsupported game system";
	private final String INVALID_MULTIPLE_ROLLS = "繰り返し対象のコマンドが実行できませんでした";
	private final String TOOMUCH_MULTIPLE_ROLLS = "繰り返し回数は1以上、100以下としてください";
	private final String UNABLED_GET_MESSAGE = "[ERROR] コマンドが成功しているにも関わらずメッセージが取得できませんでした";

	public DicerollResult(String text, String system, boolean secret, boolean rolled) {
		this.text = text;
		this.secret = secret;
		this.rolled = rolled;
		this.system = system;
		this.isError = false;
	}

	public DicerollResult(String text, String system, boolean secret, boolean rolled, boolean error) {
		this.text = text;
		this.secret = secret;
		this.rolled = rolled;
		this.system = system;
		this.isError = error;
	}

	private Boolean isRolled(JsonObject json) {
		String text = json.getString("text", "");
		if(text.startsWith(INVALID_MULTIPLE_ROLLS) || text.startsWith(TOOMUCH_MULTIPLE_ROLLS)) {
			return false;
		}
		return json.getBoolean("ok", false);
	}

	public DicerollResult(String json, String usedSystem) throws IOException {
		try {
			JsonObject result = Json.parse(json).asObject();
			this.rolled = isRolled(result);
			if(rolled) {
				this.system = usedSystem;
				this.text = result.getString("text", UNABLED_GET_MESSAGE);
				this.secret = result.getBoolean("secret", false);
				this.isError = false;				
			} else {
				this.system = "";
				this.secret = false;
				String text = result.getString("reason", "");

				if(text.equals(UNSUPPORTED_DICEBOT)) {
					this.isError = true;
					this.text = String.format("対応していないシステム ( `%s` ) を使っているようです。スペルが間違っている、または未対応のシステムかもしれません。対応しているシステムを `bcdice set システム名` で設定してください。ダイスボットの一覧を参照するには `bcdice list` をご利用ください", usedSystem);
				} else {
					this.isError = false;
					this.text = "";
				}
			}
		} catch (ParseException e) {
			throw new IOException(String.format("取得した結果のパースに失敗しました json: %s", json), e);
		}
	}

	public DicerollResult(String json) throws IOException {
		this(json, "DiceBot");
	}

	public String getText() {
		return text;
	}

	public String getSystem() {
		return system;
	}

	public boolean isSecret() {
		return secret;
	}

	public boolean isRolled() {
		return rolled;
	}

	public boolean isError() {
		return isError;
	}

	public String toString() {
		if(secret) {
			return "[Secret Dice]";
		} else {
			return text;
		}
	}
}
