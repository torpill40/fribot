package com.torpill.fribot.util;

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
	 * Rejoindre les chaines de caractères d'un tableau selon les indices de de
	 * début et de fin.
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
}
