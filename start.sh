#!/bin/sh

DISCORD_BOT_TOKEN=
BCDICE_API_URL=
IGNORE_ERROR=
# BCDICE_PASSWORD=PleaseChangeMeIfYouUseThis
# BCDICE_API_SECONDARY=http://secondary.bcdice-api.yourdomain.co.jp/
# BCDICE_DEFAULT_SYSTEM=DiceBot

java -jar discord-bcdicebot.jar "$DISCORD_BOT_TOKEN" "$BCDICE_API_URL" "$IGNORE_ERROR"
