package com.torpill.fribot.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.torpill.fribot.util.math.Matrix4f;
import com.torpill.fribot.util.math.Vector2f;
import com.torpill.fribot.util.math.Vector4f;

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

	/**
	 *
	 * Multiplier les couleurs d'une image.
	 *
	 * @param source
	 *            : image source dont on veut multiplier les couleurs
	 * @param r0
	 *            : multiplicateur rouge
	 * @param g0
	 *            : multiplicateur vert
	 * @param b0
	 *            : multiplicateur bleu
	 * @return nouvelle image
	 *
	 * @see java.awt.image.BufferedImage
	 */
	public static BufferedImage multiply(final BufferedImage source, final float r0, final float g0, final float b0) {

		final int width = source.getWidth(), height = source.getHeight();

		final BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		final int pixels[] = ((DataBufferInt) res.getRaster().getDataBuffer()).getData();
		for (int i = 0; i < width; i++) {

			for (int j = 0; j < height; j++) {

				final int pix = i + j * width;
				final int alpha = pixels[pix] >> 24 & 255;
				if (alpha < 255) {

					final int rgb = source.getRGB(i, j);

					final int r1 = rgb >> 16 & 255;
					final int red = (int) (r1 * r0);

					final int g1 = rgb >> 8 & 255;
					final int green = (int) (g1 * g0);

					final int b1 = rgb & 255;
					final int blue = (int) (b1 * b0);

					pixels[pix] = 0xFF000000 | red << 16 | green << 8 | blue;
				}
			}
		}

		return res;
	}

	/**
	 *
	 * Ajouter du bruit sur une image.
	 *
	 * @param source
	 *            : image sur laquelle on veut ajouter du bruit
	 * @param intensity
	 *            : intensité du bruit (0 : faible intensité -> 1 : forte intensité)
	 * @param saturation
	 *            : saturation du bruit (0 : pas de décoloration -> 1 :
	 *            décoloration)
	 * @param dispersion
	 *            : dispersion du bruit (0 : pas de bruit -> 1 : bruit sur tous les
	 *            pixels)
	 * @return image bruitée
	 *
	 * @see java.awt.image.BufferedImage
	 */
	public static BufferedImage noise(final BufferedImage source, final float intensity, final float saturation, final float dispersion) {

		final int width = source.getWidth(), height = source.getHeight();

		final BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		final int pixels[] = ((DataBufferInt) res.getRaster().getDataBuffer()).getData();
		for (int i = 0; i < width; i++) {

			for (int j = 0; j < height; j++) {

				final int pix = i + j * width;

				final int rgb1 = source.getRGB(i, j);
				if (Math.random() < dispersion) {

					final int r1 = rgb1 >> 16 & 255;
					final int g1 = rgb1 >> 8 & 255;
					final int b1 = rgb1 & 255;

					final int rgb0 = Color.HSBtoRGB((float) Math.random(), saturation, (float) Math.random());

					final int r0 = rgb0 >> 16 & 255;
					final int g0 = rgb0 >> 8 & 255;
					final int b0 = rgb0 & 255;

					int red = (int) (r0 * intensity + r1);
					int green = (int) (g0 * intensity + g1);
					int blue = (int) (b0 * intensity + b1);

					if (red > 255) red -= red - 255;
					if (green > 255) green -= green - 255;
					if (blue > 255) blue -= blue - 255;

					if (red < 0) red -= red * 2;
					if (green < 0) green -= green * 2;
					if (blue < 0) blue -= blue * 2;

					pixels[pix] = 0xFF000000 | red << 16 | green << 8 | blue;

				} else {

					pixels[pix] = rgb1;
				}
			}
		}

		return res;
	}

	/**
	 *
	 * Ecrire un texte sur une image.
	 *
	 * @param source
	 *            : image sur laquelle on écrit le texte
	 * @param text
	 *            : texte à écrire
	 * @param x
	 *            : position en abscisse du texte
	 * @param y
	 *            : position en ordonnée du texte
	 * @param font
	 *            : fonte du texte
	 * @param color
	 *            : couleur du texte
	 * @return image avec le texte
	 *
	 * @see java.awt.image.BufferedImage
	 * @see java.awt.Font
	 * @see java.awt.Color
	 */
	public static BufferedImage write(final BufferedImage source, final String text, final int x, final int y, final Font font, final Color color) {

		final BufferedImage res = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
		final Graphics g = res.getGraphics();
		g.setColor(color);
		g.setFont(font);
		g.drawImage(source, 0, 0, null);
		g.drawString(text, x, y);
		g.dispose();

		return res;
	}

	/**
	 *
	 * Récupérer le calque rouge d'une image.
	 *
	 * @param source
	 *            : image dont on souhaite récupérer le calque rouge
	 * @return calque rouge de l'image
	 *
	 * @see java.awt.image.BufferedImage
	 */
	public static BufferedImage redMask(final BufferedImage source) {

		return ImageProcessor.multiply(source, 1F, 0F, 0F);
	}

	/**
	 *
	 * Récupérer le calque vert d'une image.
	 *
	 * @param source
	 *            : image dont on souhaite récupérer le calque vert
	 * @return calque vert de l'image
	 *
	 * @see java.awt.image.BufferedImage
	 */
	public static BufferedImage greenMask(final BufferedImage source) {

		return ImageProcessor.multiply(source, 0F, 1F, 0F);
	}

	/**
	 *
	 * Récupérer le calque bleu d'une image.
	 *
	 * @param source
	 *            : image dont on souhaite récupérer le calque bleu
	 * @return calque bleu de l'image
	 *
	 * @see java.awt.image.BufferedImage
	 */
	public static BufferedImage blueMask(final BufferedImage source) {

		return ImageProcessor.multiply(source, 0F, 0F, 1F);
	}

	/**
	 *
	 * Superposer des calques RVB
	 *
	 * @param redM
	 *            : calque rouge
	 * @param redX
	 *            : offset en abscisse du calque rouge
	 * @param redY
	 *            : offset en ordonnée du calque rouge
	 * @param greenM
	 *            : calque vert
	 * @param greenX
	 *            : offset en abscisse du calque vert
	 * @param greenY
	 *            : offset en ordonnée du calque vert
	 * @param blueM
	 *            : calque bleu
	 * @param blueX
	 *            : offset en abscisse du calque bleu
	 * @param blueY
	 *            : offset en ordonnée du calque bleu
	 * @return image avec les masques superposés
	 *
	 * @see java.awt.image.BufferedImage
	 */
	public static BufferedImage applyRGBMasks(final BufferedImage redM, final int redX, final int redY, final BufferedImage greenM, final int greenX, final int greenY, final BufferedImage blueM, final int blueX, final int blueY) {

		final int width = redM.getWidth(), height = redM.getHeight();

		final BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		final int pixels[] = ((DataBufferInt) res.getRaster().getDataBuffer()).getData();
		for (int i = 0; i < width; i++) {

			for (int j = 0; j < height; j++) {

				final int pix = i + j * width;

				int redI = i - redX, redJ = j - redY;
				int greenI = i - greenX, greenJ = j - greenY;
				int blueI = i - blueX, blueJ = j - blueY;

				if (redI < 0) redI = -redI;
				if (redI >= width) redI -= (redI - width) * 2 + 1;
				if (redJ < 0) redJ = -redJ;
				if (redJ >= height) redJ -= (redJ - height) * 2 + 1;

				if (greenI < 0) greenI = -greenI;
				if (greenI >= width) greenI -= (greenI - width) * 2 + 1;
				if (greenJ < 0) greenJ = -greenJ;
				if (greenJ >= height) greenJ -= (greenJ - height) * 2 + 1;

				if (blueI < 0) blueI = -blueI;
				if (blueI >= width) blueI -= (blueI - width) * 2 + 1;
				if (blueJ < 0) blueJ = -blueJ;
				if (blueJ >= height) blueJ -= (blueJ - height) * 2 + 1;

				final int red = redM.getRGB(redI, redJ) & 0x00FF0000;
				final int green = greenM.getRGB(greenI, greenJ) & 0x0000FF00;
				final int blue = blueM.getRGB(blueI, blueJ) & 0x000000FF;

				pixels[pix] = 0xFF000000 | red | green | blue;
			}
		}

		return res;
	}

	/**
	 *
	 * Superposer des calques RVB d'une image source.
	 *
	 * @param source
	 *            : image source
	 * @param redX
	 *            : offset en abscisse du calque rouge
	 * @param redY
	 *            : offset en ordonnée du calque rouge
	 * @param greenX
	 *            : offset en abscisse du calque vert
	 * @param greenY
	 *            : offset en ordonnée du calque vert
	 * @param blueX
	 *            : offset en abscisse du calque bleu
	 * @param blueY
	 *            : offset en ordonnée du calque bleu
	 * @return image avec les masques superposés
	 *
	 * @see java.awt.image.BufferedImage
	 */
	public static BufferedImage applyRGBMasks(final BufferedImage source, final int redX, final int redY, final int greenX, final int greenY, final int blueX, final int blueY) {

		final int width = source.getWidth(), height = source.getHeight();

		final BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		final int pixels[] = ((DataBufferInt) res.getRaster().getDataBuffer()).getData();
		for (int i = 0; i < width; i++) {

			for (int j = 0; j < height; j++) {

				final int pix = i + j * width;

				int redI = i - redX, redJ = j - redY;
				int greenI = i - greenX, greenJ = j - greenY;
				int blueI = i - blueX, blueJ = j - blueY;

				if (redI < 0) redI = -redI;
				if (redI >= width) redI -= (redI - width) * 2 + 1;
				if (redJ < 0) redJ = -redJ;
				if (redJ >= height) redJ -= (redJ - height) * 2 + 1;

				if (greenI < 0) greenI = -greenI;
				if (greenI >= width) greenI -= (greenI - width) * 2 + 1;
				if (greenJ < 0) greenJ = -greenJ;
				if (greenJ >= height) greenJ -= (greenJ - height) * 2 + 1;

				if (blueI < 0) blueI = -blueI;
				if (blueI >= width) blueI -= (blueI - width) * 2 + 1;
				if (blueJ < 0) blueJ = -blueJ;
				if (blueJ >= height) blueJ -= (blueJ - height) * 2 + 1;

				final int red = source.getRGB(redI, redJ) & 0x00FF0000;
				final int green = source.getRGB(greenI, greenJ) & 0x0000FF00;
				final int blue = source.getRGB(blueI, blueJ) & 0x000000FF;

				pixels[pix] = 0xFF000000 | red | green | blue;
			}
		}

		return res;
	}

	/**
	 *
	 * Convertir une image en niveaux de gris.
	 *
	 * @param source
	 *            : image à convertir en niveaux de gris.
	 * @return image en niveaux de gris
	 *
	 * @see java.awt.image.BufferedImage
	 */
	public static BufferedImage grayScale(final BufferedImage source) {

		final int width = source.getWidth(), height = source.getHeight();

		final BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		final int pixels[] = ((DataBufferInt) res.getRaster().getDataBuffer()).getData();
		for (int i = 0; i < width; i++) {

			for (int j = 0; j < height; j++) {

				final int pix = i + j * width;

				final int rgb = source.getRGB(i, j);

				final int red = (int) ((rgb >> 16 & 255) * 0.299F);
				final int green = (int) ((rgb >> 8 & 255) * 0.587F);
				final int blue = (int) ((rgb & 255) * 0.114F);
				final int gray = red + green + blue;

				pixels[pix] = 0xFF000000 | gray << 16 | gray << 8 | gray;
			}
		}

		return res;
	}

	/**
	 *
	 * Projeter une image dans un espace en 3D.
	 *
	 * @param source
	 *            : image que l'on veut projeter
	 * @param transform
	 *            : matrice de transformation 3D
	 * @param z
	 *            : position de l'image en profondeur
	 * @return : image projetée
	 *
	 * @see java.awt.image.BufferedImage
	 * @see com.torpill.fribot.util.math.Matrix4f
	 */
	public static BufferedImage projectImage(final BufferedImage source, final Matrix4f transform, final float z) {

		final int width = source.getWidth();
		final int height = source.getHeight();
		final float ar = (float) width / (float) height;
		final Matrix4f projection = Matrix4f.projection(90, width, height);
		final Matrix4f zTranslate = Matrix4f.translate(0, 0, z);
		final int doubleWidth = width * 2;
		final int doubleHeight = height * 2;

		final BufferedImage res = new BufferedImage(doubleWidth, doubleHeight, BufferedImage.TYPE_INT_ARGB);
		final int pixels[] = ((DataBufferInt) res.getRaster().getDataBuffer()).getData();
		for (int i = 0; i < pixels.length; i++) pixels[i] = 0x00FF0000;

		//@formatter:off

		final Vector2f tl = new Vector2f(-ar,  1);
		final Vector2f tr = new Vector2f( ar,  1);
		final Vector2f bl = new Vector2f(-ar, -1);
		final Vector2f br = new Vector2f( ar, -1);

		//@formatter:on

		final Vector4f worldTL = zTranslate.multiply(transform.multiply(new Vector4f(tl.getX(), tl.getY(), 0, 1)));
		final Vector4f worldTR = zTranslate.multiply(transform.multiply(new Vector4f(tr.getX(), tr.getY(), 0, 1)));
		final Vector4f worldBL = zTranslate.multiply(transform.multiply(new Vector4f(bl.getX(), bl.getY(), 0, 1)));
		final Vector4f worldBR = zTranslate.multiply(transform.multiply(new Vector4f(br.getX(), br.getY(), 0, 1)));

		final Vector4f clipTL = projection.multiply(worldTL);
		final Vector4f clipTR = projection.multiply(worldTR);
		final Vector4f clipBL = projection.multiply(worldBL);
		final Vector4f clipBR = projection.multiply(worldBR);

		final Vector4f ndcTL = clipTL.scale(1F / clipTL.getW());
		final Vector4f ndcTR = clipTR.scale(1F / clipTR.getW());
		final Vector4f ndcBL = clipBL.scale(1F / clipBL.getW());
		final Vector4f ndcBR = clipBR.scale(1F / clipBR.getW());

		final float accuracy = 1F / 4F;

		for (float i = 0; i < width; i += accuracy) {

			for (float j = 0; j < height; j += accuracy) {

				final float wxa = (width - i) / width;
				final float wxb = i / width;
				final float wya = (height - j) / height;
				final float wyb = j / height;

				final float wa = wxa * wya;
				final float wb = wxb * wya;
				final float wc = wxa * wyb;
				final float wd = wxb * wyb;

				final float ndcx = wa * ndcTL.getX() + wb * ndcTR.getX() + wc * ndcBL.getX() + wd * ndcBR.getX();
				final float ndcy = wa * ndcTL.getY() + wb * ndcTR.getY() + wc * ndcBL.getY() + wd * ndcBR.getY();
				final float ndcz = wa * ndcTL.getZ() + wb * ndcTR.getZ() + wc * ndcBL.getZ() + wd * ndcBR.getZ();
				final Vector4f ndc = new Vector4f(ndcx, ndcy, ndcz, 1);
				final Vector2f dc = ndc.toDeviceCoordinatesFromNDC(width, height);

				final int x = (int) dc.getX();
				final int y = (int) dc.getY();
				if (x >= -width / 2 && x < width + width / 2 && y >= -height / 2 && y < height + height / 2 && ndc.getZ() >= -1 && ndc.getZ() < 1) {

					final int pix = y * doubleWidth + x + width / 2 + width * height;
					if (pixels[pix] == 0x00FF0000) pixels[pix] = source.getRGB((int) i, (int) j);
				}
			}
		}

		return res;
	}

	/**
	 *
	 * Redimensionner une image selon un pourcentage.
	 *
	 * @param source
	 *            : image à redimensionner
	 * @param percent
	 *            : pourcentage d'aggrandissement
	 * @return image redimensionnée
	 *
	 * @see java.awt.image.BufferedImage
	 */
	public static BufferedImage resize(final BufferedImage source, final int percent) {

		final int width = source.getWidth() * percent / 100;
		final int height = source.getHeight() * percent / 100;
		final BufferedImage res = new BufferedImage(width, height, source.getType());
		final Graphics g = res.getGraphics();
		g.drawImage(source, 0, 0, width, height, null);
		g.dispose();

		return res;
	}

	/**
	 *
	 * Arrondir les coins d'une image.
	 *
	 * @param source
	 *            : image dont on veut arrondir les coins
	 * @param radius
	 *            : rayon de l'arrondi en pixels
	 * @return image avec les coins arrondis
	 *
	 * @see java.awt.image.BufferedImage
	 */
	public static BufferedImage makeRoundedCorner(final BufferedImage source, final int radius) {

		final int width = source.getWidth();
		final int height = source.getHeight();
		final BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2 = res.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		g2.fill(new RoundRectangle2D.Float(0, 0, width, height, radius, radius));
		g2.setComposite(AlphaComposite.SrcIn);
		g2.drawImage(source, 0, 0, null);
		g2.dispose();

		return res;
	}
}
