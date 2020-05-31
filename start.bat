@echo off
cd /d %~dp0

set DISCORD_BOT_TOKEN=
set BCDICE_API_URL=
set IGNORE_ERROR=
REM set BCDICE_PASSWORD=PleaseChangeMeIfYouUseThis
REM set BCDICE_API_SECONDARY=http://secondary.bcdice-api.yourdomain.co.jp/
REM set BCDICE_DEFAULT_SYSTEM=DiceBot

java -jar discord-bcdicebot.jar %DISCORD_BOT_TOKEN% %BCDICE_API_URL% %IGNORE_ERROR%
pause
