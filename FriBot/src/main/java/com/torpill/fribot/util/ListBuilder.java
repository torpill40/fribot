package com.torpill.fribot.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Cette classe utilitaire permet la création de liste plus simplement.
 * 
 * @author torpill40
 *
 */

public class ListBuilder {

	/**
	 * 
	 * Créer une liste avec les éléments passés en arguments.
	 * 
	 * @param <T>
	 *            : type d'élements.
	 * @param items
	 *            : élements à mettre dans la liste.
	 * @return liste d'élements
	 */
	@SafeVarargs
	public static <T> List<T> listOf(final T... items) {

		List<T> list = new ArrayList<>();
		for (T item : items) {

			list.add(item);
		}

		return list;
	}
}
