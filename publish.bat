@echo off
cd /d %~dp0

del /Q discord-bcdicebot
mkdir discord-bcdicebot
copy target\discord-bcdicebot-jar-with-dependencies.jar discord-bcdicebot
copy start.bat discord-bcdicebot
copy index.html discord-bcdicebot

cd discord-bcdicebot

rename discord-bcdicebot-jar-with-dependencies.jar discord-bcdicebot.jar
rename index.html README.html

cd ../
pause