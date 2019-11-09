TARGET := discord-bcdicebot.jar

.PHONY: jar clean

jar:
	mvn install
	cp target/discord-bcdicebot-jar-with-dependencies.jar $(TARGET)

clean:
	$(RM) $(TARGET)
	mvn clean
