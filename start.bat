@echo off
cd /d %~dp0

set DISCORD_BOT_TOKEN=
set BCDICE_API_URL=
set IGNORE_ERROR=

java -jar discord-bcdicebot.jar %DISCORD_BOT_TOKEN% %BCDICE_API_URL% %IGNORE_ERROR%
pause
