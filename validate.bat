CHCP 65001
@echo off
cd /d %~dp0

echo =====When you ask to developer, please share this results / 質問する際はこの結果を開発者に送ってください=====
echo ===Ensuring java is installed / Java が入っているか確認===
java -version
if %errorlevel% neq 0 (
    echo Java is not installed. / Java がインストールされていません
    pause
    exit /b
)
echo OK
echo .
echo .
echo .

for /f "usebackq delims=" %%A in (`findstr /B set start.bat`) do %%A

echo ===Ensuring Token is valid / Token の妥当性確認===
curl -H "Authorization:Bot %DISCORD_BOT_TOKEN%" https://discordapp.com/api/users/@me --fail > nul
if %errorlevel% neq 0 (
    echo Invalid Token. / Token の値が間違っています
    pause
    exit /b
)
echo OK
echo .
echo .
echo .

echo ===Ensuring MESSAGE_INTENT is set / MESSAGE_INTENT が有効か確認===
java -jar discord-bcdicebot.jar --check-intent %DISCORD_BOT_TOKEN%
if %errorlevel% neq 0 (
	if not defined BCDICE_STANDARD_INPUT_DISABLED (
		echo INTENT_MESSAGE is not set. Set from Developer Portal. / INTENT_MESSAGE が設定されていません。Developer Portal から設定してください
		pause
		exit /b	
	) else (
		echo 不要
	)
) else (
	echo OK
)
echo .
echo .
echo .

echo ===BCDice-API server is valid / BCDice-API サーバの妥当性確認===
curl %BCDICE_API_URL% --fail -s > tmp
if %errorlevel% neq 0 (
    echo BCDice-API server is invalid / BCDice-API サーバの URL が間違っているか、サーバが停止しています
    echo URL: %BCDICE_API_URL%
    pause
    exit /b
)
find "Hello. This is BCDice-API." tmp > nul
if %errorlevel% neq 0 (
    echo BCDice-API server is invalid / BCDice-API サーバの URL が間違っているか、サーバが停止しています
    echo URL: %BCDICE_API_URL%
    pause
    exit /b
)
echo OK
echo .
echo .
echo .
echo No problems are found in start.bat / start.bat 記載の内容について問題は見つかりませんでした。
pause
