package com.torpill.fribot.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;

import com.torpill.fribot.App;

public class FileUtils {

	public static JSONObject readJSONFile(final String path) {

		if (path == null) return null;
		if (!path.endsWith(".json")) return null;

		try {

			final File file = new File(path);
			if (!file.exists()) return null;
			final FileInputStream in = new FileInputStream(file);
			final BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			final StringBuilder builder = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {

				builder.append(line + "\n");
			}

			reader.close();

			final String json = builder.toString();
			if (json.isEmpty()) return null;

			return new JSONObject(json);

		} catch (final IOException e) {

			App.LOGGER.error("Une erreur est survenue :", e);
		}

		return null;
	}
}
