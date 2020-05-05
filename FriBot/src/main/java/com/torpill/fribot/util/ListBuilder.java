package com.torpill.fribot.util;

import java.util.ArrayList;
import java.util.List;

public class ListBuilder {

	@SafeVarargs
	public static <T> List<T> listOf(final T... items) {

		List<T> list = new ArrayList<>();
		for (T item : items) {

			list.add(item);
		}

		return list;
	}
}
