package com.torpill.fribot.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 *
 * Cette classe permet de manipuler des images.
 *
 * @author torpill40
 *
 * @see java.awt.image.BufferedImage
 *
 */
public class ImageProcessor {

	/**
	 *
	 * Appliquer un masque à une image.
	 *
	 * @param source
	 *            : image sur laquelle on applique le masque
	 * @param mask
	 *            : image servant de masque
	 * @param maskX
	 *            : abscisse du point de départ du masque
	 * @param maskY
	 *            : ordonnée du point de départ du masque
	 * @param maskWidth
	 *            : largeur du masque
	 * @param maskHeight
	 *            : hauteur du masque
	 * @return image avec le masque
	 *
	 * @see java.awt.image.BufferedImage
	 */
	public static BufferedImage applyMask(final BufferedImage source, final BufferedImage mask, final int maskX, final int maskY, final int maskWidth, final int maskHeight) {

		final int width = source.getWidth(), height = source.getHeight();

		final BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics g = res.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();

		final int pixels[] = ((DataBufferInt) res.getRaster().getDataBuffer()).getData();
		for (int i = 0; i < width; i++) {

			for (int j = 0; j < height; j++) {

				final int pix = i + j * width;
				final int alpha = pixels[pix] >> 24 & 255;
				if (alpha < 255) {

					final int rgb = mask.getRGB((i - maskX) * mask.getWidth() / maskWidth, (j - maskY) * mask.getHeight() / maskHeight);

					final int r1 = rgb >> 16 & 255, r2 = pixels[pix] >> 16 & 255;
					final int red = (r1 * (255 - alpha) + r2 * alpha) / 255;

					final int g1 = rgb >> 8 & 255, g2 = pixels[pix] >> 8 & 255;
					final int green = (g1 * (255 - alpha) + g2 * alpha) / 255;

					final int b1 = rgb & 255, b2 = pixels[pix] & 255;
					final int blue = (b1 * (255 - alpha) + b2 * alpha) / 255;

					pixels[pix] = 0xFF000000 | red << 16 | green << 8 | blue;
				}
			}
		}

		return res;
	}
}
