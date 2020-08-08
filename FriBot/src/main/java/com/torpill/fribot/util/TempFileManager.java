package com.torpill.fribot.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.torpill.fribot.App;

public class TempFileManager {

	private final String TEMP_PATH = App.SRC + "/temp/";
	private final File TEMP_FILE = new File(this.TEMP_PATH);
	private final Map<Long, Map<String, File>> allTempFiles = new HashMap<>();

	public long createInstance() {

		final long id = System.currentTimeMillis();
		this.allTempFiles.put(id, new HashMap<>());
		return id;
	}

	public void addFile(final long id, final String key, final String file) {

		if (!this.TEMP_FILE.exists()) this.TEMP_FILE.mkdir();
		final File temp = new File(this.TEMP_PATH + file);
		final Map<String, File> tempFiles = this.allTempFiles.get(id);
		tempFiles.putIfAbsent(key, temp);
	}

	public File getTempFile(final long id, final String key) {

		final Map<String, File> tempFiles = this.allTempFiles.get(id);
		return tempFiles.get(key);
	}

	public String getTempFilePath(final long id, final String key) {

		return this.getTempFile(id, key).getAbsolutePath();
	}

	public void deleteAll(final long id) {

		final Map<String, File> tempFiles = this.allTempFiles.get(id);
		tempFiles.forEach((key, file) -> file.delete());
		tempFiles.clear();
		this.allTempFiles.remove(id);
		if (this.TEMP_FILE.list().length == 0) this.TEMP_FILE.delete();
	}
}
