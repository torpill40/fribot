package com.torpill.fribot.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import com.torpill.fribot.App;

/**
 *
 * Cette classe permet de faire des chargements de fonte en local.
 *
 * @author torpill40
 *
 * @see java.awt.Font
 *
 */

public class FontLoader {

	/**
	 *
	 * Charger une fonte en local.<br>
	 * Charge la fonte par défaut si la fonte recherhée est innexistante.
	 *
	 * @param path
	 *            : chemin relatif depuis le dossier dde fontes vers la fonte
	 * @return fonte chargée
	 */
	public static Font loadFont(final String path) {

		try {

			final InputStream stream = FontLoader.class.getResourceAsStream("/assets/" + App.APP_ID + "/fonts/" + path);
			if (stream == null) throw new IOException();
			return Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(Font.PLAIN, 16F);

		} catch (FontFormatException | IOException e) {

			App.LOGGER.warn("Fonte introuvable {}", "/assets/" + App.APP_ID + "/fonts/" + path);
		}

		return new Font(null, Font.PLAIN, 16);
	}
}
