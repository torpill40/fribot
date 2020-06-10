package com.torpill.fribot.util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.torpill.fribot.App;

/**
 *
 * Cette classe permet de faire des chargements d'image en local.
 *
 * @author torpill40
 *
 * @see java.awt.image.BufferedImage
 *
 */

public class ImageLoader {

	/**
	 *
	 * Générer l'image par défaut.
	 *
	 * @return image par défaut
	 *
	 * @see java.awt.image.BuffereImage
	 */
	public static BufferedImage loadDefaultImage() {

		final int WIDTH = 256, HEIGHT = 192;

		final BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		final int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
		for (int i = 0; i < WIDTH; i++) {

			for (int j = 0; j < HEIGHT; j++) {

				if (i >= WIDTH / 2 ^ j < HEIGHT / 2) {

					pixels[i + j * WIDTH] = (255 << 16) + 255;
				}
			}
		}
		return img;
	}

	/**
	 *
	 * Charger une image en local.<br>
	 * Charge l'image par défaut si l'image est innexistante.
	 *
	 * @param path
	 *            : chemin relatif depuis le dossier d'images vers l'image
	 * @return image chargée
	 *
	 * @see java.awt.image.BufferedImage
	 */
	public static BufferedImage loadImage(final String path) {

		try {

			final URL url = ImageLoader.class.getResource("/assets/" + App.APP_ID + "/images/" + path);
			if (url == null) throw new IOException();
			return ImageIO.read(url);

		} catch (final IOException e) {

			App.LOGGER.warn("Image introuvable {}", "/assets/" + App.APP_ID + "/images/" + path);
		}

		return loadDefaultImage();
	}
}
