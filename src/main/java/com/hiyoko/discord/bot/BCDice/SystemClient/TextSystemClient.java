package com.hiyoko.discord.bot.BCDice.SystemClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSystemClient implements SystemClient {
	private static final Pattern GAMESYSTEM_ROOM_PAIR_REGEXP = Pattern.compile("^(\\d*):(.*)");
	private static final String DEFAULT_EXPORT_DIR = "BCDiceConfiguration";
	private static final String EXPORT_FILE_NAME = "SystemList";
	private final String dirPath;

	private void createDirctory() {
		File confirm = new File(dirPath);
		if( ! confirm.exists() ) {
			confirm.mkdir();
		}
	}

	public TextSystemClient(String path) {
		dirPath = path;
		createDirctory();
	}
	public TextSystemClient() {
		dirPath = DEFAULT_EXPORT_DIR;
		createDirctory();
	}

	private String getExportFile() {
		return String.format("%s/%s", dirPath, EXPORT_FILE_NAME);
	}

	@Override
	public int exportSystemList(Map<String, String> systems) throws IOException {
		File exportFile = new File(getExportFile());
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, String> roomSystem : systems.entrySet() ) {
			sb.append(String.format("%s:%s\n", roomSystem.getKey(), roomSystem.getValue()));
		}
		try (
				OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(exportFile), "UTF-8");
				PrintWriter pw = new PrintWriter(new BufferedWriter(osw));) {
			pw.print(sb.toString());
		} catch(IOException e) {
			throw new IOException("部屋-システムを記録したファイルの書き出しに失敗しました", e);
		}
		return 0;
	}

	@Override
	public Map<String, String> getSystemList() throws IOException {
		Map<String, String> result = new HashMap<String, String>();
		try {
			List<String> fileLines = Files.readAllLines(FileSystems.getDefault().getPath(dirPath, EXPORT_FILE_NAME));
			for(String line: fileLines) {
				Matcher matchResult = GAMESYSTEM_ROOM_PAIR_REGEXP.matcher(line);
				if(matchResult.find()) {
					result.put(matchResult.group(1), matchResult.group(2));
				}
			}
		} catch (IOException e) {
			throw new IOException("部屋-システムを記録したファイルの読み込みに失敗しました", e);
		}	
		return result;
	}
}
