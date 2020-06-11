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

	private static double gaussianModel(final double x, final double y, final double variance) {

		return 1 / (2 * Math.PI * Math.pow(variance, 2)) * Math.exp(-(Math.pow(x, 2) + Math.pow(y, 2)) / (2 * Math.pow(variance, 2)));
	}

	private static double[] generateWeightMatrix(final int radius, final double variance) {

		final double[] weights = new double[radius * radius];
		double sum = 0;

		for (int i = 0; i < radius; i++) {

			for (int j = 0; j < radius; j++) {

				weights[i + j * radius] = ImageProcessor.gaussianModel(i - radius / 2, j - radius / 2, variance);
				sum += weights[i + j * radius];
			}
		}

		for (int i = 0; i < radius; i++) {

			for (int j = 0; j < radius; j++) {

				weights[i + j * radius] /= sum;
			}
		}

		return weights;
	}

	/**
	 *
	 * Créer un flou gaussien sur une image.
	 *
	 * @param source
	 *            : image source
	 * @param radius
	 *            : rayon d'impact du flou gaussien
	 * @param variance
	 *            : intensité du flou
	 * @return image floutée
	 *
	 * @see java.awt.image.BufferedImage
	 */
	public static BufferedImage createGaussianBlur(final BufferedImage source, final int radius, final double variance) {

		final double weights[] = ImageProcessor.generateWeightMatrix(radius, variance);
		final BufferedImage res = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < source.getWidth(); i++) {

			for (int j = 0; j < source.getHeight(); j++) {

				final double[] weightsRed = new double[radius * radius];
				final double[] weightsGreen = new double[radius * radius];
				final double[] weightsBlue = new double[radius * radius];

				for (int weightX = 0; weightX < radius; weightX++) {

					for (int weightY = 0; weightY < radius; weightY++) {

						int sampleX = i + weightX - radius / 2;
						int sampleY = j + weightY - radius / 2;

						if (sampleX > source.getWidth() - 1) {

							final int offset = sampleX - (source.getWidth() - 1);
							sampleX = source.getWidth() - 1 - offset;
						}

						if (sampleY > source.getHeight() - 1) {

							final int offset = sampleY - (source.getHeight() - 1);
							sampleY = source.getHeight() - 1 - offset;
						}

						if (sampleX < 0) {

							sampleX = -sampleX;
						}

						if (sampleY < 0) {

							sampleY = -sampleY;
						}

						final double currentWeight = weights[weightX + weightY * radius];

						final int sampledColor = source.getRGB(sampleX, sampleY);

						weightsRed[weightX + weightY * radius] = currentWeight * (sampledColor >> 16 & 0xFF);
						weightsGreen[weightX + weightY * radius] = currentWeight * (sampledColor >> 8 & 0xFF);
						weightsBlue[weightX + weightY * radius] = currentWeight * (sampledColor & 0xFF);
					}
				}

				final int red = ImageProcessor.getWeightedColorValue(weightsRed);
				final int green = ImageProcessor.getWeightedColorValue(weightsGreen);
				final int blue = ImageProcessor.getWeightedColorValue(weightsBlue);
				final int rgb = red << 16 | green << 8 | blue;
				res.setRGB(i, j, rgb);
			}
		}

		return res;
	}

	private static int getWeightedColorValue(final double[] weightedColor) {

		double sum = 0;

		for (int i = 0; i < weightedColor.length; i++) {

			sum += weightedColor[i];
		}

		return (int) sum;
	}
}
