package com.torpill.fribot.util;

import java.text.Normalizer;

/**
 *
 * Cette classe permet de travailler avec des chaines de caractères.
 *
 * @author torpill40
 *
 */

public class StringProcessor {

	/**
	 *
	 * Rejoindre les chaines de caractères d'un tableau selon les indices de début
	 * et de fin.
	 *
	 * @param array
	 *            : tableau de chaine de caractères
	 * @param start
	 *            : indice de début
	 * @param end
	 *            : indice de fin
	 * @return chaine de caractère
	 *
	 * @throws IndexOutOfBoundsException
	 */
	public static String join(final String[] array, final int start, final int end) throws IndexOutOfBoundsException {

		final StringBuilder builder = new StringBuilder();
		for (int i = start; i <= end; i++) {

			builder.append(array[i]);
			if (i < end) builder.append(" ");
		}
		return builder.toString();
	}

	/**
	 *
	 * Enlever tous les accents d'une chaine de caractères.
	 *
	 * @param text
	 *            : chaine de caractère dont on veut retirer les accents
	 * @return chaine de caractère sans accents
	 */
	public static String removeAccent(final String text) {

		return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
	}
}
